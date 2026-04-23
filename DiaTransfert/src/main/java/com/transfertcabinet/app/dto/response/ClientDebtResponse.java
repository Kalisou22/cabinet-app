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
public class ClientDebtResponse {
    private Long clientId;
    private String clientNom;
    private String clientTelephone;
    private BigDecimal totalDebt;
    private LocalDate nextDueDate;
    private Integer pendingTransactionsCount;
}