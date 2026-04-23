package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.ClientRequest;
import com.transfertcabinet.app.dto.response.ClientResponse;
import com.transfertcabinet.app.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        log.info("REST request to create Client: {}", request.getTelephone());
        ClientResponse response = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id,
                                                       @Valid @RequestBody ClientRequest request) {
        log.info("REST request to update Client: {}", id);
        ClientResponse response = clientService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long id) {
        log.info("REST request to get Client: {}", id);
        ClientResponse response = clientService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients(
            @RequestParam(required = false) Boolean activeOnly) {
        log.info("REST request to get all Clients, activeOnly: {}", activeOnly);
        List<ClientResponse> responses = Boolean.TRUE.equals(activeOnly) ?
                clientService.findAllActive() : clientService.findAll();
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<ClientResponse>> getAllClientsPaginated(Pageable pageable) {
        log.info("REST request to get paginated Clients");
        Page<ClientResponse> responses = clientService.findAllPaginated(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ClientResponse>> searchClients(@RequestParam String telephone) {
        log.info("REST request to search Clients by telephone: {}", telephone);
        List<ClientResponse> responses = clientService.searchByTelephone(telephone);
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Clients endettés (métier)
    @GetMapping("/in-debt")
    public ResponseEntity<List<ClientResponse>> getClientsInDebt() {
        log.info("REST request to get clients with debt");
        List<ClientResponse> responses = clientService.findClientsWithDebt();
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("REST request to delete Client: {}", id);
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}