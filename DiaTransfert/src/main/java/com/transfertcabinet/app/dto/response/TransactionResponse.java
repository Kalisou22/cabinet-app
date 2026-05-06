package com.transfertcabinet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long accountId;
    private String accountName;

    // ✅ Pour compatibilité frontend (DEPOT / RETRAIT)
    private String type;

    // ✅ Pour compatibilité frontend (CASH / CREDIT)
    private String nature;

    private BigDecimal amount;
    private String description;
    private String username;
    private LocalDateTime transactionDate;
}