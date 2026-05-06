package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.LegacyTransactionRequest;
import com.transfertcabinet.app.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {
    TransactionResponse createFromLegacy(LegacyTransactionRequest request);
    TransactionResponse findById(Long id);
    Page<TransactionResponse> findAll(Pageable pageable);
    BigDecimal getClientBalance(Long clientId);
    BigDecimal getClientDebt(Long clientId);
}