package com.transfertcabinet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSummaryResponse {
    private Long clientId;
    private String clientName;
    private String clientPhone;
    private BigDecimal balance;
    private BigDecimal debt;
    private boolean hasDebt;
    private boolean hasAdvance;
}