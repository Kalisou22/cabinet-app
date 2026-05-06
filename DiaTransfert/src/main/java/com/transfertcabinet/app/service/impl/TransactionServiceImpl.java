package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.LegacyTransactionRequest;
import com.transfertcabinet.app.dto.response.TransactionResponse;
import com.transfertcabinet.app.entity.*;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.TransactionMapper;
import com.transfertcabinet.app.repository.*;
import com.transfertcabinet.app.service.TransactionService;
import com.transfertcabinet.app.service.balance.BalanceCalculator;
import com.transfertcabinet.app.service.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionValidator transactionValidator;
    private final BalanceCalculator balanceCalculator;

    @Override
    public TransactionResponse createFromLegacy(LegacyTransactionRequest request) {
        log.info("Creating transaction from legacy request");

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getAccountId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        TransactionType mappedType = transactionMapper.mapLegacy(
                request.getLegacyType(),
                request.getLegacyNature()
        );

        transactionValidator.validate(mappedType, client, account, request.getAmount());

        Transaction transaction = Transaction.builder()
                .client(client)
                .account(account)
                .type(mappedType)
                .amount(request.getAmount())
                .description(request.getDescription())
                .user(user)
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        log.info("Transaction created with id: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientBalance(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        return balanceCalculator.getClientBalance(client);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientDebt(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        return balanceCalculator.getClientDebt(client);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        // ✅ Utiliser le mapper pour convertir TransactionType en legacy (DEPOT/RETRAIT)
        String legacyType = transactionMapper.mapTypeToLegacy(transaction.getType());
        String legacyNature = transactionMapper.mapNatureToLegacy(transaction.getType()).name();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .clientId(transaction.getClient().getId())
                .clientName(transaction.getClient().getNom())
                .accountId(transaction.getAccount().getId())
                .accountName(transaction.getAccount().getName())
                .type(legacyType)           // ✅ Maintenant c'est un String "DEPOT" ou "RETRAIT"
                .nature(legacyNature)       // ✅ "CASH" ou "CREDIT"
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .username(transaction.getUser().getUsername())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}