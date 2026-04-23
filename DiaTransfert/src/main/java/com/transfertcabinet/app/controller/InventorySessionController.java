package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.InventorySessionRequest;
import com.transfertcabinet.app.dto.response.InventorySessionResponse;
import com.transfertcabinet.app.dto.response.InventorySummaryResponse;
import com.transfertcabinet.app.service.InventorySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/inventory/sessions")
@RequiredArgsConstructor
public class InventorySessionController {

    private final InventorySessionService sessionService;

    @PostMapping
    public ResponseEntity<InventorySessionResponse> createSession(@Valid @RequestBody InventorySessionRequest request) {
        log.info("REST request to create Inventory Session for date: {}", request.getDate());
        InventorySessionResponse response = sessionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventorySessionResponse> updateSession(@PathVariable Long id, @Valid @RequestBody InventorySessionRequest request) {
        log.info("REST request to update Inventory Session: {}", id);
        InventorySessionResponse response = sessionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventorySessionResponse> getSession(@PathVariable Long id) {
        log.info("REST request to get Inventory Session: {}", id);
        InventorySessionResponse response = sessionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<InventorySessionResponse>> getAllSessions() {
        log.info("REST request to get all Inventory Sessions");
        List<InventorySessionResponse> responses = sessionService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<InventorySessionResponse> getSessionByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("REST request to get Inventory Session by date: {}", date);
        InventorySessionResponse response = sessionService.findByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/open")
    public ResponseEntity<List<InventorySessionResponse>> getOpenSessions() {
        log.info("REST request to get open Inventory Sessions");
        List<InventorySessionResponse> responses = sessionService.findOpenSessions();
        return ResponseEntity.ok(responses);
    }

    // ✅ CORRIGÉ : Accepte justification dans le body
    @PostMapping("/{id}/close")
    public ResponseEntity<InventorySessionResponse> closeSession(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> payload) {
        log.info("REST request to close Inventory Session: {}", id);
        String justification = payload != null ? payload.get("justification") : null;
        InventorySessionResponse response = sessionService.closeSession(id, justification);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<InventorySummaryResponse> getSessionSummary(@PathVariable Long id) {
        log.info("REST request to get summary for Inventory Session: {}", id);
        InventorySummaryResponse response = sessionService.getSummary(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        log.info("REST request to delete Inventory Session: {}", id);
        sessionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}