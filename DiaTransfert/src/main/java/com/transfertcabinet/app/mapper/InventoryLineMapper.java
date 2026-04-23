package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.InventoryLineRequest;
import com.transfertcabinet.app.dto.response.InventoryLineResponse;
import com.transfertcabinet.app.entity.InventoryLine;
import com.transfertcabinet.app.entity.InventorySession;
import org.springframework.stereotype.Component;

import java.time.LocalDate;  // ← AJOUTÉ

@Component
public class InventoryLineMapper {

    public InventoryLine toEntity(InventoryLineRequest request, InventorySession session) {
        if (request == null) {
            return null;
        }

        return InventoryLine.builder()
                .session(session)
                .platformName(request.getPlatformName())
                .montantMatin(request.getMontantMatin())
                .montantSoir(request.getMontantSoir())
                .build();
    }

    public InventoryLineResponse toResponse(InventoryLine line) {
        if (line == null) {
            return null;
        }

        return InventoryLineResponse.builder()
                .id(line.getId())
                .sessionId(line.getSession() != null ? line.getSession().getId() : null)
                .sessionDate(line.getSession() != null ? line.getSession().getDate() : null)
                .platformName(line.getPlatformName())
                .montantMatin(line.getMontantMatin())
                .montantSoir(line.getMontantSoir())
                .build();
    }

    public void updateEntity(InventoryLine line, InventoryLineRequest request, InventorySession session) {
        if (request.getPlatformName() != null) {
            line.setPlatformName(request.getPlatformName());
        }
        if (request.getMontantMatin() != null) {
            line.setMontantMatin(request.getMontantMatin());
        }
        if (request.getMontantSoir() != null) {
            line.setMontantSoir(request.getMontantSoir());
        }
        if (session != null) {
            line.setSession(session);
        }
    }
}