package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Account;
import com.transfertcabinet.app.repository.ClientRepository;
import com.transfertcabinet.app.repository.AccountRepository;
import com.transfertcabinet.app.service.BalanceService;
import com.transfertcabinet.app.service.balance.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceCalculator balanceCalculator;
    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    @Override
    public BigDecimal getClientBalance(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + clientId));
        return balanceCalculator.getClientBalance(client);
    }

    @Override
    public BigDecimal getClientDebt(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé: " + clientId));
        return balanceCalculator.getClientDebt(client);
    }

    @Override
    public List<Client> getClientsInDebt() {
        return clientRepository.findAll().stream()
                .filter(c -> balanceCalculator.getClientDebt(c).compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getAccountBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + accountId));
        return balanceCalculator.getAccountBalance(account);
    }
}