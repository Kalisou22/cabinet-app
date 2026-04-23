package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.CashOperationRequest;
import com.transfertcabinet.app.dto.response.CashOperationResponse;
import com.transfertcabinet.app.dto.response.CashOperationSummaryResponse;
import com.transfertcabinet.app.enums.CashOperationType;
import com.transfertcabinet.app.service.CashOperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/cash-operations")  // ✅ URL COMPLÈTE
@RequiredArgsConstructor
public class CashOperationController {

    private final CashOperationService cashOperationService;

    @PostMapping
    public ResponseEntity<CashOperationResponse> createOperation(@Valid @RequestBody CashOperationRequest request) {
        log.info("REST request to create Cash Operation");
        CashOperationResponse response = cashOperationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CashOperationResponse> updateOperation(@PathVariable Long id, @Valid @RequestBody CashOperationRequest request) {
        log.info("REST request to update Cash Operation: {}", id);
        CashOperationResponse response = cashOperationService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CashOperationResponse> getOperation(@PathVariable Long id) {
        log.info("REST request to get Cash Operation: {}", id);
        CashOperationResponse response = cashOperationService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CashOperationResponse>> getAllOperations() {
        log.info("REST request to get all Cash Operations");
        List<CashOperationResponse> responses = cashOperationService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CashOperationResponse>> getOperationsByType(@PathVariable CashOperationType type) {
        log.info("REST request to get Cash Operations by type: {}", type);
        List<CashOperationResponse> responses = cashOperationService.findByType(type);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/period")
    public ResponseEntity<List<CashOperationResponse>> getOperationsByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get Cash Operations between {} and {}", start, end);
        List<CashOperationResponse> responses = cashOperationService.findByPeriod(start, end);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/summary")
    public ResponseEntity<CashOperationSummaryResponse> getOperationsSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get Cash Operations summary between {} and {}", start, end);
        CashOperationSummaryResponse response = cashOperationService.getSummaryByPeriod(start, end);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        log.info("REST request to delete Cash Operation: {}", id);
        cashOperationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}