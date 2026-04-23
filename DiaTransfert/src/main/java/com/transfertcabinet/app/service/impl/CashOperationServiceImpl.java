package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.CashOperationRequest;
import com.transfertcabinet.app.dto.response.CashOperationResponse;
import com.transfertcabinet.app.dto.response.CashOperationSummaryResponse;
import com.transfertcabinet.app.entity.CashOperation;
import com.transfertcabinet.app.entity.OperationCategory;
import com.transfertcabinet.app.entity.User;
import com.transfertcabinet.app.enums.CashOperationType;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.CashOperationMapper;
import com.transfertcabinet.app.repository.CashOperationRepository;
import com.transfertcabinet.app.repository.OperationCategoryRepository;
import com.transfertcabinet.app.repository.UserRepository;
import com.transfertcabinet.app.service.CashOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CashOperationServiceImpl implements CashOperationService {

    private final CashOperationRepository cashOperationRepository;
    private final OperationCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CashOperationMapper cashOperationMapper;

    @Override
    public CashOperationResponse create(CashOperationRequest request) {
        log.info("Creating new cash operation of type: {}", request.getType());

        OperationCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("OperationCategory", "id", request.getCategoryId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        CashOperation operation = cashOperationMapper.toEntity(request, category, user);

        try {
            CashOperation savedOperation = cashOperationRepository.save(operation);
            log.info("Cash operation created successfully with ID: {}", savedOperation.getId());
            return cashOperationMapper.toResponse(savedOperation);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    public CashOperationResponse update(Long id, CashOperationRequest request) {
        log.info("Updating cash operation with ID: {}", id);

        CashOperation operation = cashOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CashOperation", "id", id));

        OperationCategory category = request.getCategoryId() != null ?
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("OperationCategory", "id", request.getCategoryId())) :
                operation.getCategory();

        User user = request.getUserId() != null ?
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId())) :
                operation.getUser();

        cashOperationMapper.updateEntity(operation, request, category, user);

        try {
            CashOperation updatedOperation = cashOperationRepository.save(operation);
            log.info("Cash operation updated successfully with ID: {}", updatedOperation.getId());
            return cashOperationMapper.toResponse(updatedOperation);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CashOperationResponse findById(Long id) {
        log.debug("Finding cash operation by ID: {}", id);

        CashOperation operation = cashOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CashOperation", "id", id));

        return cashOperationMapper.toResponse(operation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashOperationResponse> findAll() {
        log.debug("Finding all cash operations");

        return cashOperationRepository.findAllActive().stream()
                .map(cashOperationMapper::toResponse)
                .collect(Collectors.toList());
    }

    // IMPROVEMENT: Pagination
    @Override
    @Transactional(readOnly = true)
    public Page<CashOperationResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated cash operations");
        return cashOperationRepository.findAllActive(pageable)
                .map(cashOperationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashOperationResponse> findByType(CashOperationType type) {
        log.debug("Finding cash operations by type: {}", type);

        return cashOperationRepository.findActiveByType(type).stream()
                .map(cashOperationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CashOperationResponse> findByPeriod(LocalDateTime start, LocalDateTime end) {
        log.debug("Finding cash operations between {} and {}", start, end);

        if (start.isAfter(end)) {
            throw new BusinessException("Start date must be before end date");
        }

        return cashOperationRepository.findByCreatedAtBetween(start, end).stream()
                .filter(op -> !op.getDeleted())
                .map(cashOperationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CashOperationSummaryResponse getSummaryByPeriod(LocalDateTime start, LocalDateTime end) {
        log.info("Getting cash operations summary between {} and {}", start, end);

        if (start.isAfter(end)) {
            throw new BusinessException("Start date must be before end date");
        }

        // FIX: Null safety BigDecimal
        BigDecimal totalEntrees = Optional.ofNullable(
                        cashOperationRepository.sumMontantByTypeAndDateBetween(
                                CashOperationType.ENTREE, start, end))
                .orElse(BigDecimal.ZERO);

        BigDecimal totalSorties = Optional.ofNullable(
                        cashOperationRepository.sumMontantByTypeAndDateBetween(
                                CashOperationType.SORTIE, start, end))
                .orElse(BigDecimal.ZERO);

        List<CashOperation> operations = cashOperationRepository.findByCreatedAtBetween(start, end);

        return cashOperationMapper.toSummaryResponse(totalEntrees, totalSorties, operations.size());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting cash operation with ID: {}", id);

        CashOperation operation = cashOperationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CashOperation", "id", id));

        // IMPROVEMENT: soft delete
        operation.setDeleted(true);
        cashOperationRepository.save(operation);

        log.info("Cash operation soft deleted successfully with ID: {}", id);
    }
}