package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.InventoryLineRequest;
import com.transfertcabinet.app.dto.response.InventoryLineResponse;
import com.transfertcabinet.app.service.InventoryLineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/inventory/lines")  // ✅ URL COMPLÈTE
@RequiredArgsConstructor
public class InventoryLineController {

    private final InventoryLineService lineService;

    @PostMapping
    public ResponseEntity<InventoryLineResponse> createLine(@Valid @RequestBody InventoryLineRequest request) {
        log.info("REST request to create Inventory Line");
        InventoryLineResponse response = lineService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryLineResponse> updateLine(@PathVariable Long id, @Valid @RequestBody InventoryLineRequest request) {
        log.info("REST request to update Inventory Line: {}", id);
        InventoryLineResponse response = lineService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryLineResponse> getLine(@PathVariable Long id) {
        log.info("REST request to get Inventory Line: {}", id);
        InventoryLineResponse response = lineService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventoryLineResponse>> getAllLines() {
        log.info("REST request to get all Inventory Lines");
        List<InventoryLineResponse> responses = lineService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<InventoryLineResponse>> getLinesBySession(@PathVariable Long sessionId) {
        log.info("REST request to get Inventory Lines for session: {}", sessionId);
        List<InventoryLineResponse> responses = lineService.findBySessionId(sessionId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLine(@PathVariable Long id) {
        log.info("REST request to delete Inventory Line: {}", id);
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}