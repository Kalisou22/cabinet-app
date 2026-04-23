package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.InventorySessionRequest;
import com.transfertcabinet.app.dto.response.InventorySessionResponse;
import com.transfertcabinet.app.dto.response.InventorySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventorySessionService {

    InventorySessionResponse create(InventorySessionRequest request);
    InventorySessionResponse update(Long id, InventorySessionRequest request);
    InventorySessionResponse findById(Long id);
    List<InventorySessionResponse> findAll();
    InventorySessionResponse findByDate(LocalDate date);

    // ✅ MODIFIÉ : Ajout du paramètre justification
    InventorySessionResponse closeSession(Long id, String justification);

    InventorySummaryResponse getSummary(Long id);
    void delete(Long id);

    Page<InventorySessionResponse> findAllPaginated(Pageable pageable);
    List<InventorySessionResponse> findOpenSessions();

    Map<String, BigDecimal> getRepartitionActuelle(Long sessionId);
    BigDecimal getTotalMatin(Long sessionId);
    BigDecimal getTotalSoir(Long sessionId);
}