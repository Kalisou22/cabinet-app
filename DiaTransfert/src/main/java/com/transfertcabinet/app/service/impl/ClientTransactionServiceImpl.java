package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.ClientTransactionRequest;
import com.transfertcabinet.app.dto.response.ClientDebtResponse;
import com.transfertcabinet.app.dto.response.ClientTransactionResponse;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.ClientTransaction;
import com.transfertcabinet.app.entity.User;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.ClientTransactionMapper;
import com.transfertcabinet.app.repository.ClientRepository;
import com.transfertcabinet.app.repository.ClientTransactionRepository;
import com.transfertcabinet.app.repository.UserRepository;
import com.transfertcabinet.app.service.ClientTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClientTransactionServiceImpl implements ClientTransactionService {

    private final ClientTransactionRepository transactionRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ClientTransactionMapper transactionMapper;

    @Override
    public ClientTransactionResponse create(ClientTransactionRequest request) {
        log.info("Creating new transaction for client ID: {}", request.getClientId());

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        validateTransaction(request);

        ClientTransaction transaction = transactionMapper.toEntity(request, client, user);
        ClientTransaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created successfully with ID: {}", savedTransaction.getId());
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

        validateTransaction(request);

        transactionMapper.updateEntity(transaction, request, client, user);
        ClientTransaction updatedTransaction = transactionRepository.save(transaction);

        log.info("Transaction updated successfully with ID: {}", updatedTransaction.getId());
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
    public Page<ClientTransactionResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated transactions");
        return transactionRepository.findAllActive(pageable)
                .map(transactionMapper::toResponse);
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

        BigDecimal totalDebt = transactionRepository.calculateTotalDebtByClientId(clientId);

        List<ClientTransaction> pendingTransactions = transactionRepository
                .findByClientIdAndStatus(clientId, TransactionStatus.EN_COURS);

        LocalDate nextDueDate = pendingTransactions.stream()
                .map(ClientTransaction::getDueDate)
                .filter(date -> date != null && date.isAfter(LocalDate.now()))
                .min(LocalDate::compareTo)
                .orElse(null);

        return transactionMapper.toDebtResponse(client, totalDebt, nextDueDate, pendingTransactions.size());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting transaction with ID: {}", id);

        ClientTransaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

        transaction.setDeleted(true);
        transactionRepository.save(transaction);

        log.info("Transaction soft deleted successfully with ID: {}", id);
    }

    // ✅ AJOUT : Calculer le solde d'un client (avance/dette)
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientSolde(Long clientId) {
        log.debug("Calculating solde for client ID: {}", clientId);

        List<ClientTransaction> creditTransactions = transactionRepository
                .findByClientIdAndNature(clientId, TransactionNature.CREDIT);

        BigDecimal totalDepots = creditTransactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOT)
                .map(ClientTransaction::getResteAPayer)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRetraits = creditTransactions.stream()
                .filter(t -> t.getType() == TransactionType.RETRAIT)
                .map(ClientTransaction::getResteAPayer)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // SOLDE = DEPOT - RETRAIT
        // > 0 : cabinet doit au client (AVANCE)
        // < 0 : client doit au cabinet (DETTE)
        return totalDepots.subtract(totalRetraits);
    }

    // ✅ AJOUT : Vérifier si un client a une relation d'avance/dette
    @Override
    @Transactional(readOnly = true)
    public boolean hasCreditRelation(Long clientId) {
        log.debug("Checking credit relation for client ID: {}", clientId);
        return transactionRepository.countByClientIdAndNature(clientId, TransactionNature.CREDIT) > 0;
    }

    private void validateTransaction(ClientTransactionRequest request) {
        if (request.getNature() == TransactionNature.CREDIT) {
            if (request.getDueDate() == null) {
                throw new BusinessException("Due date is required for credit transactions");
            }
            if (request.getDueDate().isBefore(LocalDate.now())) {
                throw new BusinessException("Due date cannot be in the past");
            }
        }

        if (request.getNature() == TransactionNature.CASH) {
            if (request.getStatus() != null && request.getStatus() == TransactionStatus.EN_COURS) {
                throw new BusinessException("Cash transaction cannot be EN_COURS");
            }
        }
    }
}