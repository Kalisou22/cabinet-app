package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.ClientTransaction;
import com.transfertcabinet.app.enums.TransactionStatus;
import com.transfertcabinet.app.enums.TransactionNature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ClientTransactionRepository extends JpaRepository<ClientTransaction, Long> {

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.client.id = :clientId AND ct.deleted = false")
    List<ClientTransaction> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.status = :status AND ct.deleted = false")
    List<ClientTransaction> findByStatus(@Param("status") TransactionStatus status);

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.nature = :nature AND ct.deleted = false")
    List<ClientTransaction> findByNature(@Param("nature") TransactionNature nature);

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.client.id = :clientId AND ct.status = :status AND ct.deleted = false")
    List<ClientTransaction> findByClientIdAndStatus(@Param("clientId") Long clientId,
                                                    @Param("status") TransactionStatus status);

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.client.id = :clientId AND ct.nature = :nature AND ct.deleted = false")
    List<ClientTransaction> findByClientIdAndNature(@Param("clientId") Long clientId,
                                                    @Param("nature") TransactionNature nature);

    @Query("SELECT COALESCE(SUM(ct.resteAPayer), 0) FROM ClientTransaction ct WHERE ct.client.id = :clientId AND ct.status = 'EN_COURS' AND ct.deleted = false")
    BigDecimal calculateTotalDebtByClientId(@Param("clientId") Long clientId);

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.deleted = false")
    List<ClientTransaction> findAllActive();

    @Query("SELECT ct FROM ClientTransaction ct WHERE ct.deleted = false")
    Page<ClientTransaction> findAllActive(Pageable pageable);

    // ✅ AJOUT : Compter les transactions CREDIT d'un client
    @Query("SELECT COUNT(ct) FROM ClientTransaction ct WHERE ct.client.id = :clientId AND ct.nature = :nature AND ct.deleted = false")
    int countByClientIdAndNature(@Param("clientId") Long clientId, @Param("nature") TransactionNature nature);
}