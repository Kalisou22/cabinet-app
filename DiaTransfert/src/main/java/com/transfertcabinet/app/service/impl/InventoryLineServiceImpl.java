package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.InventoryLineRequest;
import com.transfertcabinet.app.dto.response.InventoryLineResponse;
import com.transfertcabinet.app.entity.InventoryLine;
import com.transfertcabinet.app.entity.InventorySession;
import com.transfertcabinet.app.enums.InventoryStatus;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.InventoryLineMapper;
import com.transfertcabinet.app.repository.InventoryLineRepository;
import com.transfertcabinet.app.repository.InventorySessionRepository;
import com.transfertcabinet.app.service.InventoryLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryLineServiceImpl implements InventoryLineService {

    private final InventoryLineRepository lineRepository;
    private final InventorySessionRepository sessionRepository;
    private final InventoryLineMapper lineMapper;

    @Override
    public InventoryLineResponse create(InventoryLineRequest request) {
        log.info("Creating new inventory line for session ID: {}", request.getSessionId());

        InventorySession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", request.getSessionId()));

        if (session.getStatus() == InventoryStatus.CLOSED) {
            throw new BusinessException("Cannot add lines to a closed session");
        }

        InventoryLine line = lineMapper.toEntity(request, session);
        InventoryLine savedLine = lineRepository.save(line);

        log.info("Inventory line created successfully with ID: {}", savedLine.getId());
        return lineMapper.toResponse(savedLine);
    }

    @Override
    public InventoryLineResponse update(Long id, InventoryLineRequest request) {
        log.info("Updating inventory line with ID: {}", id);

        InventoryLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLine", "id", id));

        if (line.getSession().getStatus() == InventoryStatus.CLOSED) {
            throw new BusinessException("Cannot modify lines of a closed session");
        }

        InventorySession session = request.getSessionId() != null ?
                sessionRepository.findById(request.getSessionId())
                        .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", request.getSessionId())) :
                line.getSession();

        // IMPROVEMENT: vérifier que la nouvelle session n'est pas fermée
        if (session.getStatus() == InventoryStatus.CLOSED) {
            throw new BusinessException("Cannot assign to a closed session");
        }

        lineMapper.updateEntity(line, request, session);
        InventoryLine updatedLine = lineRepository.save(line);

        log.info("Inventory line updated successfully with ID: {}", updatedLine.getId());
        return lineMapper.toResponse(updatedLine);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryLineResponse findById(Long id) {
        log.debug("Finding inventory line by ID: {}", id);

        InventoryLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLine", "id", id));

        return lineMapper.toResponse(line);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryLineResponse> findAll() {
        log.debug("Finding all inventory lines");

        return lineRepository.findAll().stream()
                .filter(line -> !line.getDeleted())
                .map(lineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryLineResponse> findBySessionId(Long sessionId) {
        log.debug("Finding inventory lines for session ID: {}", sessionId);

        return lineRepository.findBySessionId(sessionId).stream()
                .filter(line -> !line.getDeleted())
                .map(lineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting inventory line with ID: {}", id);

        InventoryLine line = lineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLine", "id", id));

        if (line.getSession().getStatus() == InventoryStatus.CLOSED) {
            throw new BusinessException("Cannot delete lines from a closed session");
        }

        // IMPROVEMENT: soft delete
        line.setDeleted(true);
        lineRepository.save(line);

        log.info("Inventory line soft deleted successfully with ID: {}", id);
    }
}