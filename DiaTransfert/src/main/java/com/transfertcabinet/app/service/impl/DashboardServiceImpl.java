package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.response.DashboardStatsResponse;
import com.transfertcabinet.app.dto.response.TransactionResponse;
import com.transfertcabinet.app.entity.Account;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Transaction;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.mapper.TransactionMapper;
import com.transfertcabinet.app.repository.AccountRepository;
import com.transfertcabinet.app.repository.ClientRepository;
import com.transfertcabinet.app.repository.TransactionRepository;
import com.transfertcabinet.app.service.DashboardService;
import com.transfertcabinet.app.service.balance.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceCalculator balanceCalculator;
    private final TransactionMapper transactionMapper;

    @Override
    public DashboardStatsResponse getStats() {
        log.debug("Calculating dashboard stats");

        // Total caisse (tous comptes actifs)
        List<Account> accounts = accountRepository.findByActiveTrue();
        BigDecimal totalCashBalance = BigDecimal.ZERO;
        for (Account account : accounts) {
            totalCashBalance = totalCashBalance.add(balanceCalculator.getAccountBalance(account));
        }

        // Clients
        List<Client> clients = clientRepository.findAllActive();
        int totalClients = clients.size();
        int clientsWithDebt = 0;
        int clientsWithAdvance = 0;
        BigDecimal totalDebt = BigDecimal.ZERO;
        BigDecimal totalAdvances = BigDecimal.ZERO;

        for (Client client : clients) {
            BigDecimal balance = balanceCalculator.getClientBalance(client);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                clientsWithDebt++;
                totalDebt = totalDebt.add(balance.abs());
            } else if (balance.compareTo(BigDecimal.ZERO) > 0) {
                clientsWithAdvance++;
                totalAdvances = totalAdvances.add(balance);
            }
        }

        // Transactions en cours (dettes non soldées)
        List<Transaction> pendingCreditTransactions = transactionRepository.findAll().stream()
                .filter(t -> t.getType() == TransactionType.ADVANCE &&
                        t.getTransactionDate().isAfter(LocalDateTime.now().minusMonths(1)))
                .toList();
        int pendingDebtsCount = pendingCreditTransactions.size();

        // Transactions du mois
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        List<Transaction> monthlyTransactions = transactionRepository.findAll().stream()
                .filter(t -> t.getTransactionDate().isAfter(startOfMonth) &&
                        t.getTransactionDate().isBefore(endOfMonth))
                .toList();
        int monthlyTransactionCount = monthlyTransactions.size();

        return DashboardStatsResponse.builder()
                .totalCashBalance(totalCashBalance)
                .totalDebt(totalDebt)
                .totalAdvances(totalAdvances)
                .totalClients(totalClients)
                .clientsWithDebt(clientsWithDebt)
                .clientsWithAdvance(clientsWithAdvance)
                .pendingDebtsCount(pendingDebtsCount)
                .monthlyTransactionCount(monthlyTransactionCount)
                .build();
    }

    @Override
    public Page<TransactionResponse> getRecentTransactions(Pageable pageable) {
        log.debug("Getting recent transactions");
        return transactionRepository.findAll(pageable)
                .map(transactionMapper::toResponse);
    }
}