package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.response.TransactionResponse;
import com.transfertcabinet.app.entity.Transaction;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionMapper {

    // ✅ Mapping FRONTEND → BACKEND (existant)
    public TransactionType mapLegacy(String legacyType, TransactionNature legacyNature) {

        if (legacyType == null || legacyNature == null) {
            throw new BusinessException("Le type et la nature de la transaction sont obligatoires");
        }

        log.debug("Mapping transaction legacy: type={}, nature={}", legacyType, legacyNature);

        if ("DEPOT".equalsIgnoreCase(legacyType)) {
            if (legacyNature == TransactionNature.CASH) {
                return TransactionType.DEPOSIT;
            }
            if (legacyNature == TransactionNature.CREDIT) {
                return TransactionType.REPAYMENT;
            }
        }

        if ("RETRAIT".equalsIgnoreCase(legacyType)) {
            if (legacyNature == TransactionNature.CASH) {
                return TransactionType.WITHDRAWAL;
            }
            if (legacyNature == TransactionNature.CREDIT) {
                return TransactionType.ADVANCE;
            }
        }

        throw new BusinessException("Combinaison de transaction invalide: " + legacyType + " + " + legacyNature);
    }

    // ✅ Mapping BACKEND → FRONTEND (NOUVEAU - CRITIQUE)
    public String mapTypeToLegacy(TransactionType backendType) {
        switch (backendType) {
            case DEPOSIT:
                return "DEPOT";
            case REPAYMENT:
                return "DEPOT";
            case WITHDRAWAL:
                return "RETRAIT";
            case ADVANCE:
                return "RETRAIT";
            default:
                throw new BusinessException("Type backend non mappable: " + backendType);
        }
    }

    public TransactionNature mapNatureToLegacy(TransactionType backendType) {
        switch (backendType) {
            case DEPOSIT:
                return TransactionNature.CASH;
            case REPAYMENT:
                return TransactionNature.CREDIT;
            case WITHDRAWAL:
                return TransactionNature.CASH;
            case ADVANCE:
                return TransactionNature.CREDIT;
            default:
                throw new BusinessException("Nature non mappable pour: " + backendType);
        }
    }

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        String legacyType = mapTypeToLegacy(transaction.getType());
        TransactionNature legacyNature = mapNatureToLegacy(transaction.getType());

        return TransactionResponse.builder()
                .id(transaction.getId())
                .clientId(transaction.getClient() != null ? transaction.getClient().getId() : null)
                .clientName(transaction.getClient() != null ? transaction.getClient().getNom() : null)
                .accountId(transaction.getAccount() != null ? transaction.getAccount().getId() : null)
                .accountName(transaction.getAccount() != null ? transaction.getAccount().getName() : null)
                .type(legacyType)
                .nature(legacyNature.name())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .username(transaction.getUser() != null ? transaction.getUser().getUsername() : null)
                .transactionDate(transaction.getTransactionDate())
                .build();
    }
}