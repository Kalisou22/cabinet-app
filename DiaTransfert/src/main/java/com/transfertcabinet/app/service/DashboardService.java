package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.response.DashboardStatsResponse;
import com.transfertcabinet.app.dto.response.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DashboardService {
    DashboardStatsResponse getStats();
    Page<TransactionResponse> getRecentTransactions(Pageable pageable);
}