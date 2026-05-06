package com.transfertcabinet.app.service;

import com.transfertcabinet.app.entity.Client;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {
    BigDecimal getClientBalance(Long clientId);
    BigDecimal getClientDebt(Long clientId);
    List<Client> getClientsInDebt();
    BigDecimal getAccountBalance(Long accountId);
}