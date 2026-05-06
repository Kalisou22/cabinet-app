package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.Transaction;
import com.transfertcabinet.app.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.client.id = :clientId AND t.type = :type")
    BigDecimal sumAmountByClientAndType(@Param("clientId") Long clientId, @Param("type") TransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.account.id = :accountId AND t.type IN :types")
    BigDecimal sumAmountByAccountAndTypeIn(@Param("accountId") Long accountId, @Param("types") List<TransactionType> types);

    Page<Transaction> findByClientId(Long clientId, Pageable pageable);
}