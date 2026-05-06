package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.response.DashboardStatsResponse;
import com.transfertcabinet.app.dto.response.TransactionResponse;
import com.transfertcabinet.app.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponse> getStats() {
        log.info("REST request to get dashboard stats");
        DashboardStatsResponse stats = dashboardService.getStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent-transactions")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST request to get {} recent transactions", limit);
        Pageable pageable = PageRequest.of(0, limit, Sort.by("transactionDate").descending());
        Page<TransactionResponse> page = dashboardService.getRecentTransactions(pageable);
        return ResponseEntity.ok(page.getContent());
    }
}