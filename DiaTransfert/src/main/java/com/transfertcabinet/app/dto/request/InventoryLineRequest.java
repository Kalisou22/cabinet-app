package com.transfertcabinet.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLineRequest {

    @NotNull(message = "L'ID de la session est obligatoire")
    private Long sessionId;

    @NotBlank(message = "Le nom de la plateforme est obligatoire")
    private String platformName;

    private BigDecimal montantMatin;

    private BigDecimal montantSoir;
}