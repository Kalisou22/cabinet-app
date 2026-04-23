package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.InventoryLineRequest;
import com.transfertcabinet.app.dto.response.InventoryLineResponse;

import java.util.List;

public interface InventoryLineService {
    InventoryLineResponse create(InventoryLineRequest request);
    InventoryLineResponse update(Long id, InventoryLineRequest request);
    InventoryLineResponse findById(Long id);
    List<InventoryLineResponse> findAll();
    List<InventoryLineResponse> findBySessionId(Long sessionId);
    void delete(Long id);
}