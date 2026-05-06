package com.transfertcabinet.app.enums;

public enum TransactionType {
    // Anciennes valeurs (pour compatibilité)
    DEPOT,
    RETRAIT,

    // Nouvelles valeurs (métier)
    DEPOSIT,
    WITHDRAWAL,
    ADVANCE,
    REPAYMENT;

    // Méthode utilitaire pour le mapping
    public boolean isDepot() {
        return this == DEPOT || this == DEPOSIT;
    }

    public boolean isRetrait() {
        return this == RETRAIT || this == WITHDRAWAL;
    }
}