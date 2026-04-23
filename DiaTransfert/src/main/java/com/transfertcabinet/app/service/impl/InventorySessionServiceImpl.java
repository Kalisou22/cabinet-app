package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.InventorySessionRequest;
import com.transfertcabinet.app.dto.response.InventorySessionResponse;
import com.transfertcabinet.app.dto.response.InventorySummaryResponse;
import com.transfertcabinet.app.entity.InventoryLine;
import com.transfertcabinet.app.entity.InventorySession;
import com.transfertcabinet.app.entity.User;
import com.transfertcabinet.app.enums.InventoryStatus;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.InventorySessionMapper;
import com.transfertcabinet.app.repository.InventoryLineRepository;
import com.transfertcabinet.app.repository.InventorySessionRepository;
import com.transfertcabinet.app.repository.UserRepository;
import com.transfertcabinet.app.service.InventorySessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventorySessionServiceImpl implements InventorySessionService {

    private final InventorySessionRepository sessionRepository;
    private final InventoryLineRepository lineRepository;
    private final UserRepository userRepository;
    private final InventorySessionMapper sessionMapper;

    @Override
    public InventorySessionResponse create(InventorySessionRequest request) {
        log.info("Creating new inventory session for date: {}", request.getDate());

        // ✅ CORRIGÉ : Vérifie UNIQUEMENT les sessions actives (non supprimées)
        // On cherche d'abord une session active (deleted=false) avec cette date
        Optional<InventorySession> existingActive = sessionRepository.findAll().stream()
                .filter(s -> s.getDate().equals(request.getDate()) && !s.getDeleted())
                .findFirst();

        if (existingActive.isPresent()) {
            InventorySession existing = existingActive.get();
            log.warn("Session already exists for date: {} with status: {}", request.getDate(), existing.getStatus());
            throw new BusinessException("Une session existe déjà pour le " + request.getDate() +
                    ". Veuillez la réinitialiser ou la fermer d'abord.");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        InventorySession session = sessionMapper.toEntity(request, user);
        InventorySession savedSession = sessionRepository.save(session);

        log.info("Inventory session created successfully with ID: {}", savedSession.getId());
        return sessionMapper.toResponse(savedSession);
    }

    @Override
    public InventorySessionResponse update(Long id, InventorySessionRequest request) {
        log.info("Updating inventory session with ID: {}", id);

        InventorySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", id));

        User user = request.getUserId() != null ?
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId())) :
                session.getUser();

        sessionMapper.updateEntity(session, request, user);
        InventorySession updatedSession = sessionRepository.save(session);

        log.info("Inventory session updated successfully with ID: {}", updatedSession.getId());
        return sessionMapper.toResponse(updatedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public InventorySessionResponse findById(Long id) {
        log.debug("Finding inventory session by ID: {}", id);

        InventorySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", id));

        return sessionMapper.toResponse(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventorySessionResponse> findAll() {
        log.debug("Finding all inventory sessions");

        return sessionRepository.findAll().stream()
                .filter(s -> !s.getDeleted())
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InventorySessionResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated inventory sessions");
        return sessionRepository.findAll(pageable)
                .map(sessionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InventorySessionResponse findByDate(LocalDate date) {
        log.debug("Finding inventory session by date: {}", date);

        // ✅ CORRIGÉ : Ne retourne que les sessions actives (non supprimées)
        InventorySession session = sessionRepository.findAll().stream()
                .filter(s -> s.getDate().equals(date) && !s.getDeleted())
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "date", date));

        return sessionMapper.toResponse(session);
    }

    @Override
    public InventorySessionResponse closeSession(Long id, String justification) {
        log.info("Closing inventory session with ID: {}", id);

        InventorySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", id));

        if (session.getStatus() == InventoryStatus.CLOSED) {
            throw new BusinessException("Session is already closed");
        }

        BigDecimal totalMatin = Optional.ofNullable(lineRepository.sumMontantMatinBySessionId(id))
                .orElse(BigDecimal.ZERO);

        BigDecimal totalSoir = Optional.ofNullable(lineRepository.sumMontantSoirBySessionId(id))
                .orElse(BigDecimal.ZERO);

        BigDecimal resultat = totalSoir.subtract(totalMatin);

        session.setStatus(InventoryStatus.CLOSED);
        session.setResultat(resultat);

        if (justification != null && !justification.trim().isEmpty()) {
            session.setJustification(justification);
        }

        InventorySession closedSession = sessionRepository.save(session);

        log.info("Inventory session closed successfully with ID: {}, Result: {}", id, resultat);
        return sessionMapper.toResponse(closedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public InventorySummaryResponse getSummary(Long id) {
        log.debug("Getting summary for inventory session ID: {}", id);

        InventorySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", id));

        BigDecimal totalMatin = Optional.ofNullable(lineRepository.sumMontantMatinBySessionId(id))
                .orElse(BigDecimal.ZERO);

        BigDecimal totalSoir = Optional.ofNullable(lineRepository.sumMontantSoirBySessionId(id))
                .orElse(BigDecimal.ZERO);

        List<InventoryLine> lines = lineRepository.findBySessionId(id);

        return sessionMapper.toSummaryResponse(session, totalMatin, totalSoir, lines.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventorySessionResponse> findOpenSessions() {
        log.debug("Finding open inventory sessions");
        return sessionRepository.findAll().stream()
                .filter(s -> s.getStatus() == InventoryStatus.OPEN && !s.getDeleted())
                .map(sessionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("HARD deleting inventory session with ID: {}", id);

        InventorySession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventorySession", "id", id));

        // Hard delete: supprimer d'abord les lignes d'inventaire
        List<InventoryLine> lines = lineRepository.findBySessionId(id);
        if (!lines.isEmpty()) {
            lineRepository.deleteAll(lines);
            log.info("Deleted {} inventory lines for session ID: {}", lines.size(), id);
        }

        // Puis supprimer la session
        sessionRepository.delete(session);

        log.info("Inventory session hard deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getRepartitionActuelle(Long sessionId) {
        log.debug("Getting current distribution for session ID: {}", sessionId);

        List<InventoryLine> lines = lineRepository.findActiveBySessionId(sessionId);

        return lines.stream()
                .collect(Collectors.toMap(
                        InventoryLine::getPlatformName,
                        line -> line.getMontantSoir() != null ? line.getMontantSoir() : line.getMontantMatin(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalMatin(Long sessionId) {
        log.debug("Getting total matin for session ID: {}", sessionId);
        return Optional.ofNullable(lineRepository.sumMontantMatinBySessionId(sessionId))
                .orElse(BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSoir(Long sessionId) {
        log.debug("Getting total soir for session ID: {}", sessionId);
        return Optional.ofNullable(lineRepository.sumMontantSoirBySessionId(sessionId))
                .orElse(BigDecimal.ZERO);
    }
}