package com.transfertcabinet.app.service.balance;

import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Account;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BalanceCalculator {

    private final TransactionRepository transactionRepository;

    public BigDecimal getClientBalance(Client client) {
        if (client == null || client.getId() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal deposits = transactionRepository.sumAmountByClientAndType(client.getId(), TransactionType.DEPOSIT);
        BigDecimal repayments = transactionRepository.sumAmountByClientAndType(client.getId(), TransactionType.REPAYMENT);
        BigDecimal withdrawals = transactionRepository.sumAmountByClientAndType(client.getId(), TransactionType.WITHDRAWAL);
        BigDecimal advances = transactionRepository.sumAmountByClientAndType(client.getId(), TransactionType.ADVANCE);

        if (deposits == null) deposits = BigDecimal.ZERO;
        if (repayments == null) repayments = BigDecimal.ZERO;
        if (withdrawals == null) withdrawals = BigDecimal.ZERO;
        if (advances == null) advances = BigDecimal.ZERO;

        return (deposits.add(repayments)).subtract(withdrawals.add(advances));
    }

    public BigDecimal getClientDebt(Client client) {
        BigDecimal balance = getClientBalance(client);
        return balance.compareTo(BigDecimal.ZERO) < 0 ? balance.abs() : BigDecimal.ZERO;
    }

    public BigDecimal getAccountBalance(Account account) {
        if (account == null || account.getId() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalIn = transactionRepository.sumAmountByAccountAndTypeIn(account.getId(),
                List.of(TransactionType.WITHDRAWAL, TransactionType.ADVANCE));
        BigDecimal totalOut = transactionRepository.sumAmountByAccountAndTypeIn(account.getId(),
                List.of(TransactionType.DEPOSIT, TransactionType.REPAYMENT));

        if (totalIn == null) totalIn = BigDecimal.ZERO;
        if (totalOut == null) totalOut = BigDecimal.ZERO;

        return totalIn.subtract(totalOut);
    }
}