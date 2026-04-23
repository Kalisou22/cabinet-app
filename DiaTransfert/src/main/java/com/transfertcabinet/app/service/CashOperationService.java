package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.CashOperationRequest;
import com.transfertcabinet.app.dto.response.CashOperationResponse;
import com.transfertcabinet.app.dto.response.CashOperationSummaryResponse;
import com.transfertcabinet.app.enums.CashOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface CashOperationService {
    CashOperationResponse create(CashOperationRequest request);
    CashOperationResponse update(Long id, CashOperationRequest request);
    CashOperationResponse findById(Long id);
    List<CashOperationResponse> findAll();
    List<CashOperationResponse> findByType(CashOperationType type);
    List<CashOperationResponse> findByPeriod(LocalDateTime start, LocalDateTime end);
    CashOperationSummaryResponse getSummaryByPeriod(LocalDateTime start, LocalDateTime end);
    void delete(Long id);

    // IMPROVEMENT: Pagination
    Page<CashOperationResponse> findAllPaginated(Pageable pageable);
}