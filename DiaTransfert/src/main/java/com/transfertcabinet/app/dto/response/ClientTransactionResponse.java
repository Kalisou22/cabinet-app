package com.transfertcabinet.app.dto.response;

import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientTransactionResponse {
    private Long id;
    private Long clientId;
    private String clientNom;
    private String clientTelephone;
    private TransactionType type;
    private TransactionNature nature;
    private BigDecimal montant;
    private BigDecimal resteAPayer;
    private LocalDate dueDate;
    private TransactionStatus status;
    private String description;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}