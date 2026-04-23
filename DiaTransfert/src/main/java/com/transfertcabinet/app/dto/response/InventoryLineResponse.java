package com.transfertcabinet.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;  // ← IMPORT MANQUANT À AJOUTER

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLineResponse {
    private Long id;
    private Long sessionId;
    private LocalDate sessionDate;  // ← Ce champ utilise LocalDate
    private String platformName;
    private BigDecimal montantMatin;
    private BigDecimal montantSoir;
}