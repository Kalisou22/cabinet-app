package com.transfertcabinet.app.dto.request;

import com.transfertcabinet.app.enums.InventoryStatus;
import jakarta.validation.constraints.NotNull;
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
public class InventorySessionRequest {

    @NotNull(message = "La date est obligatoire")
    private LocalDate date;

    private BigDecimal resultat;

    private InventoryStatus status;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    // ✅ NOUVEAU : Justification pour la fermeture
    private String justification;
}