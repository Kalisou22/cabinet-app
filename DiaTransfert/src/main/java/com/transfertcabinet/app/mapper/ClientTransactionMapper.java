package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.ClientTransactionRequest;
import com.transfertcabinet.app.dto.response.ClientDebtResponse;
import com.transfertcabinet.app.dto.response.ClientTransactionResponse;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.ClientTransaction;
import com.transfertcabinet.app.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class ClientTransactionMapper {

    public ClientTransaction toEntity(ClientTransactionRequest request, Client client, User user) {
        if (request == null) {
            return null;
        }

        return ClientTransaction.builder()
                .client(client)
                .type(request.getType())
                .nature(request.getNature())
                .montant(request.getMontant())
                .resteAPayer(request.getResteAPayer())
                .dueDate(request.getDueDate())
                .status(request.getStatus())
                .description(request.getDescription())
                .user(user)
                .build();
    }

    public ClientTransactionResponse toResponse(ClientTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return ClientTransactionResponse.builder()
                .id(transaction.getId())
                .clientId(transaction.getClient() != null ? transaction.getClient().getId() : null)
                .clientNom(transaction.getClient() != null ? transaction.getClient().getNom() : null)
                .clientTelephone(transaction.getClient() != null ? transaction.getClient().getTelephone() : null)
                .type(transaction.getType())
                .nature(transaction.getNature())
                .montant(transaction.getMontant())
                .resteAPayer(transaction.getResteAPayer())
                .dueDate(transaction.getDueDate())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)
                .username(transaction.getUser() != null ? transaction.getUser().getUsername() : null)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    public void updateEntity(ClientTransaction transaction, ClientTransactionRequest request, Client client, User user) {
        if (request.getType() != null) {
            transaction.setType(request.getType());
        }
        if (request.getNature() != null) {
            transaction.setNature(request.getNature());
        }
        if (request.getMontant() != null) {
            transaction.setMontant(request.getMontant());
        }
        if (request.getResteAPayer() != null) {
            transaction.setResteAPayer(request.getResteAPayer());
        }
        if (request.getDueDate() != null) {
            transaction.setDueDate(request.getDueDate());
        }
        if (request.getStatus() != null) {
            transaction.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }
        if (client != null) {
            transaction.setClient(client);
        }
        if (user != null) {
            transaction.setUser(user);
        }
    }

    public ClientDebtResponse toDebtResponse(Client client, BigDecimal totalDebt,
                                             LocalDate nextDueDate, Integer pendingCount) {
        return ClientDebtResponse.builder()
                .clientId(client.getId())
                .clientNom(client.getNom())
                .clientTelephone(client.getTelephone())
                .totalDebt(totalDebt)
                .nextDueDate(nextDueDate)
                .pendingTransactionsCount(pendingCount)
                .build();
    }
}