package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.ClientTransactionRequest;
import com.transfertcabinet.app.dto.response.ClientDebtResponse;
import com.transfertcabinet.app.dto.response.ClientTransactionResponse;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ClientTransactionService {
    ClientTransactionResponse create(ClientTransactionRequest request);
    ClientTransactionResponse update(Long id, ClientTransactionRequest request);
    ClientTransactionResponse findById(Long id);
    List<ClientTransactionResponse> findAll();
    List<ClientTransactionResponse> findByClientId(Long clientId);
    List<ClientTransactionResponse> findByStatus(TransactionStatus status);
    List<ClientTransactionResponse> findByNature(TransactionNature nature);
    ClientDebtResponse calculateClientDebt(Long clientId);
    void delete(Long id);

    Page<ClientTransactionResponse> findAllPaginated(Pageable pageable);

    // ✅ AJOUT : Calculer le solde d'un client (avance/dette)
    // Solde > 0 : cabinet doit au client (AVANCE)
    // Solde < 0 : client doit au cabinet (DETTE)
    BigDecimal getClientSolde(Long clientId);

    // ✅ AJOUT : Vérifier si un client a une relation d'avance/dette
    boolean hasCreditRelation(Long clientId);
}