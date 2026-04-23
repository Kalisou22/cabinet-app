package com.transfertcabinet.app.dto.response;

import com.transfertcabinet.app.enums.CashOperationType;
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
public class CashOperationResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal montant;
    private CashOperationType type;
    private String description;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}