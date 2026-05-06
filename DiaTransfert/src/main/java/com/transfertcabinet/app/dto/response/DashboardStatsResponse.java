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
public class DashboardStatsResponse {
    private BigDecimal totalCashBalance;      // Solde caisse (tous comptes)
    private BigDecimal totalDebt;              // Total dettes clients
    private BigDecimal totalAdvances;          // Total avances clients
    private Integer totalClients;              // Nombre total clients
    private Integer clientsWithDebt;           // Clients endettés
    private Integer clientsWithAdvance;        // Clients avec avance
    private Integer pendingDebtsCount;         // Transactions CREDIT en cours
    private Integer monthlyTransactionCount;   // Transactions du mois
}