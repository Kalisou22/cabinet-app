package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.ClientTransactionRequest;
import com.transfertcabinet.app.dto.response.ClientDebtResponse;
import com.transfertcabinet.app.dto.response.ClientTransactionResponse;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.service.ClientTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/transactions")
@RequiredArgsConstructor
public class ClientTransactionController {

    private final ClientTransactionService transactionService;

    @PostMapping
    public ResponseEntity<ClientTransactionResponse> createTransaction(
            @Valid @RequestBody ClientTransactionRequest request) {
        log.info("REST request to create Transaction for client: {}", request.getClientId());
        ClientTransactionResponse response = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientTransactionResponse> updateTransaction(
            @PathVariable Long id, @Valid @RequestBody ClientTransactionRequest request) {
        log.info("REST request to update Transaction: {}", id);
        ClientTransactionResponse response = transactionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientTransactionResponse> getTransaction(@PathVariable Long id) {
        log.info("REST request to get Transaction: {}", id);
        ClientTransactionResponse response = transactionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ClientTransactionResponse>> getAllTransactions() {
        log.info("REST request to get all Transactions");
        List<ClientTransactionResponse> responses = transactionService.findAll();
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<ClientTransactionResponse>> getAllTransactionsPaginated(Pageable pageable) {
        log.info("REST request to get paginated Transactions");
        Page<ClientTransactionResponse> responses = transactionService.findAllPaginated(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ClientTransactionResponse>> getTransactionsByClient(
            @PathVariable Long clientId) {
        log.info("REST request to get Transactions for client: {}", clientId);
        List<ClientTransactionResponse> responses = transactionService.findByClientId(clientId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClientTransactionResponse>> getTransactionsByStatus(
            @PathVariable TransactionStatus status) {
        log.info("REST request to get Transactions by status: {}", status);
        List<ClientTransactionResponse> responses = transactionService.findByStatus(status);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/nature/{nature}")
    public ResponseEntity<List<ClientTransactionResponse>> getTransactionsByNature(
            @PathVariable TransactionNature nature) {
        log.info("REST request to get Transactions by nature: {}", nature);
        List<ClientTransactionResponse> responses = transactionService.findByNature(nature);
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Dette client (métier)
    @GetMapping("/debt/{clientId}")
    public ResponseEntity<ClientDebtResponse> getClientDebt(@PathVariable Long clientId) {
        log.info("REST request to calculate debt for client: {}", clientId);
        ClientDebtResponse response = transactionService.calculateClientDebt(clientId);
        return ResponseEntity.ok(response);
    }

    // IMPROVEMENT: Liste des dettes en cours
    @GetMapping("/pending-debts")
    public ResponseEntity<List<ClientTransactionResponse>> getPendingDebts() {
        log.info("REST request to get all pending debts");
        List<ClientTransactionResponse> responses = transactionService.findByStatus(TransactionStatus.EN_COURS);
        return ResponseEntity.ok(responses);
    }

    // ✅ AJOUT : Récupérer le solde d'un client (avance/dette)
    // Solde > 0 : cabinet doit au client (AVANCE)
    // Solde < 0 : client doit au cabinet (DETTE)
    @GetMapping("/solde/{clientId}")
    public ResponseEntity<BigDecimal> getClientSolde(@PathVariable Long clientId) {
        log.info("REST request to get solde for client: {}", clientId);
        BigDecimal solde = transactionService.getClientSolde(clientId);
        return ResponseEntity.ok(solde);
    }

    // ✅ AJOUT : Vérifier si un client a une relation de dette/avance
    @GetMapping("/has-credit-relation/{clientId}")
    public ResponseEntity<Boolean> hasCreditRelation(@PathVariable Long clientId) {
        log.info("REST request to check credit relation for client: {}", clientId);
        boolean hasRelation = transactionService.hasCreditRelation(clientId);
        return ResponseEntity.ok(hasRelation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("REST request to delete Transaction: {}", id);
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}