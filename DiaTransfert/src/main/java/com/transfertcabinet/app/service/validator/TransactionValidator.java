package com.transfertcabinet.app.service.validator;

import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Account;
import com.transfertcabinet.app.enums.TransactionType;  // ✅ Changé
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.service.balance.BalanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionValidator {

    private final BalanceCalculator balanceCalculator;

    public void validate(TransactionType type, Client client, Account account, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le montant doit être strictement positif");
        }

        if (client == null || client.getActif() == null || !client.getActif()) {
            throw new BusinessException("Client invalide ou inactif");
        }

        if (account == null || account.getActive() == null || !account.getActive()) {
            throw new BusinessException("Compte invalide ou inactif");
        }

        if (type == TransactionType.WITHDRAWAL) {
            BigDecimal clientBalance = balanceCalculator.getClientBalance(client);
            if (clientBalance.compareTo(amount) < 0) {
                throw new BusinessException("Solde client insuffisant pour un retrait");
            }
        }

        if (type == TransactionType.REPAYMENT) {
            BigDecimal clientBalance = balanceCalculator.getClientBalance(client);
            if (clientBalance.compareTo(BigDecimal.ZERO) >= 0) {
                throw new BusinessException("Impossible de rembourser car le client n'a pas de dette");
            }
            BigDecimal debt = clientBalance.abs();
            if (amount.compareTo(debt) > 0) {
                throw new BusinessException("Montant de remboursement supérieur à la dette");
            }
        }

        if (type == TransactionType.ADVANCE) {
            log.info("Création d'une avance (crédit sans dépôt) pour le client: {}", client.getNom());
        }
    }
}