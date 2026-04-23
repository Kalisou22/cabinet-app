package com.transfertcabinet.app.dto.request;

import com.transfertcabinet.app.enums.CashOperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashOperationRequest {

    @NotNull(message = "L'ID de la catégorie est obligatoire")
    private Long categoryId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotNull(message = "Le type est obligatoire")
    private CashOperationType type;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
}