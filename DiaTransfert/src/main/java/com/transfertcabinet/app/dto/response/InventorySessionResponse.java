package com.transfertcabinet.app.dto.response;

import com.transfertcabinet.app.enums.InventoryStatus;
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
public class InventorySessionResponse {
    private Long id;
    private LocalDate date;
    private BigDecimal resultat;
    private InventoryStatus status;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // ✅ NOUVEAU : Justification
    private String justification;
}