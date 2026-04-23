package com.transfertcabinet.app.dto.request;

import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class ClientTransactionRequest {

    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;

    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType type;

    @NotNull(message = "La nature de la transaction est obligatoire")
    private TransactionNature nature;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private BigDecimal resteAPayer;

    private LocalDate dueDate;

    private TransactionStatus status;

    private String description;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
}