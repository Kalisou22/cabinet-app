package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.ClientTransactionRequest;
import com.transfertcabinet.app.dto.response.ClientDebtResponse;
import com.transfertcabinet.app.dto.response.ClientTransactionResponse;
import com.transfertcabinet.app.entity.*;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.ClientTransactionMapper;
import com.transfertcabinet.app.mapper.TransactionMapper;
import com.transfertcabinet.app.repository.*;
import com.transfertcabinet.app.service.ClientTransactionService;
import com.transfertcabinet.app.service.balance.BalanceCalculator;
import com.transfertcabinet.app.service.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientTransactionServiceImpl implements ClientTransactionService {

    private final ClientTransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository newTransactionRepository;
    private final ClientTransactionMapper transactionMapper;
    private final TransactionMapper newTransactionMapper;
    private final TransactionValidator transactionValidator;
    private final BalanceCalculator balanceCalculator;

    private static final Long DEFAULT_ACCOUNT_ID = 1L;

    @Override
    public ClientTransactionResponse create(ClientTransactionRequest request) {
        log.info("Creating transaction for client ID: {}", request.getClientId());

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Account account = accountRepository.findById(DEFAULT_ACCOUNT_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", DEFAULT_ACCOUNT_ID));

        // Convertir le type legacy (DEPOT/RETRAIT) vers le nouveau type métier
        String legacyTypeName = request.getType().name(); // "DEPOT" ou "RETRAIT"
        TransactionType newType = newTransactionMapper.mapLegacy(legacyTypeName, request.getNature());

        // Valider la transaction
        transactionValidator.validate(newType, client, account, request.getMontant());

        // Créer la nouvelle transaction (table transactions)
        Transaction newTransaction = Transaction.builder()
                .client(client)
                .account(account)
                .type(newType)
                .amount(request.getMontant())
                .description(request.getDescription())
                .user(user)
                .transactionDate(LocalDateTime.now())
                .build();

        newTransactionRepository.save(newTransaction);

        // Garder l'ancienne table pour compatibilité frontend
        ClientTransaction transaction = transactionMapper.toEntity(request, client, user);

        if (request.getNature() == TransactionNature.CASH) {
            transaction.setStatus(TransactionStatus.REMBOURSE);
            transaction.setResteAPayer(BigDecimal.ZERO);
        } else {
            transaction.setStatus(TransactionStatus.EN_COURS);
            if (request.getResteAPayer() == null) {
                transaction.setResteAPayer(request.getMontant());
            }
        }

        ClientTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", savedTransaction.getId());

        return transactionMapper.toResponse(savedTransaction);
    }

    @Override
    public ClientTransactionResponse update(Long id, ClientTransactionRequest request) {
        log.info("Updating transaction with ID: {}", id);

        ClientTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        Client client = request.getClientId() != null ?
                clientRepository.findById(request.getClientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId())) :
                transaction.getClient();

        User user = request.getUserId() != null ?
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId())) :
                transaction.getUser();

        transactionMapper.updateEntity(transaction, request, client, user);

        ClientTransaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated with ID: {}", updatedTransaction.getId());

        return transactionMapper.toResponse(updatedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientTransactionResponse findById(Long id) {
        log.debug("Finding transaction by ID: {}", id);
        ClientTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTransactionResponse> findAll() {
        log.debug("Finding all transactions");
        return transactionRepository.findAllActive().stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTransactionResponse> findByClientId(Long clientId) {
        log.debug("Finding transactions for client ID: {}", clientId);
        return transactionRepository.findByClientId(clientId).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTransactionResponse> findByStatus(TransactionStatus status) {
        log.debug("Finding transactions by status: {}", status);
        return transactionRepository.findByStatus(status).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientTransactionResponse> findByNature(TransactionNature nature) {
        log.debug("Finding transactions by nature: {}", nature);
        return transactionRepository.findByNature(nature).stream()
                .map(transactionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDebtResponse calculateClientDebt(Long clientId) {
        log.info("Calculating debt for client ID: {}", clientId);

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));

        BigDecimal debt = balanceCalculator.getClientDebt(client);

        List<ClientTransaction> pendingTransactions = transactionRepository
                .findByClientIdAndStatus(clientId, TransactionStatus.EN_COURS);

        LocalDate nextDueDate = pendingTransactions.stream()
                .map(ClientTransaction::getDueDate)
                .filter(date -> date != null && date.isAfter(LocalDate.now()))
                .min(LocalDate::compareTo)
                .orElse(null);

        return transactionMapper.toDebtResponse(client, debt, nextDueDate, pendingTransactions.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientTransactionResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated transactions");
        return transactionRepository.findAllActive(pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientSolde(Long clientId) {
        log.debug("Calculating solde for client ID: {}", clientId);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", clientId));
        return balanceCalculator.getClientBalance(client);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCreditRelation(Long clientId) {
        log.debug("Checking credit relation for client ID: {}", clientId);
        return transactionRepository.countByClientIdAndNature(clientId, TransactionNature.CREDIT) > 0;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting transaction with ID: {}", id);
        ClientTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
        log.info("Transaction soft deleted with ID: {}", id);
    }
}