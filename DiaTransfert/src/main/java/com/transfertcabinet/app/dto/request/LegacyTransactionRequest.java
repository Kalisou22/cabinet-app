package com.transfertcabinet.app.dto.request;

import com.transfertcabinet.app.enums.TransactionNature;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegacyTransactionRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long accountId;

    @NotNull
    private String legacyType;  // "DEPOT" ou "RETRAIT" (string du frontend)

    @NotNull
    private TransactionNature legacyNature;  // CASH ou CREDIT

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;

    @NotNull
    private Long userId;
}