package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.InventorySessionRequest;
import com.transfertcabinet.app.dto.response.InventorySessionResponse;
import com.transfertcabinet.app.dto.response.InventorySummaryResponse;
import com.transfertcabinet.app.entity.InventorySession;
import com.transfertcabinet.app.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class InventorySessionMapper {

    public InventorySession toEntity(InventorySessionRequest request, User user) {
        if (request == null) {
            return null;
        }

        return InventorySession.builder()
                .date(request.getDate())
                .resultat(request.getResultat())
                .status(request.getStatus())
                .user(user)
                .justification(request.getJustification())  // ✅ NOUVEAU
                .build();
    }

    public InventorySessionResponse toResponse(InventorySession session) {
        if (session == null) {
            return null;
        }

        return InventorySessionResponse.builder()
                .id(session.getId())
                .date(session.getDate())
                .resultat(session.getResultat())
                .status(session.getStatus())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .username(session.getUser() != null ? session.getUser().getUsername() : null)
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .justification(session.getJustification())  // ✅ NOUVEAU
                .build();
    }

    public void updateEntity(InventorySession session, InventorySessionRequest request, User user) {
        if (request.getDate() != null) {
            session.setDate(request.getDate());
        }
        if (request.getResultat() != null) {
            session.setResultat(request.getResultat());
        }
        if (request.getStatus() != null) {
            session.setStatus(request.getStatus());
        }
        if (user != null) {
            session.setUser(user);
        }
        if (request.getJustification() != null) {
            session.setJustification(request.getJustification());
        }
    }

    public InventorySummaryResponse toSummaryResponse(InventorySession session, BigDecimal totalMatin,
                                                      BigDecimal totalSoir, Integer platformCount) {
        return InventorySummaryResponse.builder()
                .sessionId(session.getId())
                .date(session.getDate())
                .totalMatin(totalMatin)
                .totalSoir(totalSoir)
                .resultat(totalSoir.subtract(totalMatin))
                .platformCount(platformCount)
                .build();
    }
}