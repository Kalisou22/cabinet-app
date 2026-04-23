package com.transfertcabinet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryResponse {
    private Long sessionId;
    private LocalDate date;
    private BigDecimal totalMatin;
    private BigDecimal totalSoir;
    private BigDecimal resultat;
    private Integer platformCount;
}