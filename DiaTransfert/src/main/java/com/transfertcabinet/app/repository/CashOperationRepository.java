package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.CashOperation;
import com.transfertcabinet.app.enums.CashOperationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CashOperationRepository extends JpaRepository<CashOperation, Long> {

    List<CashOperation> findByType(CashOperationType type);
    List<CashOperation> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<CashOperation> findByCategoryId(Long categoryId);

    @Query("SELECT COALESCE(SUM(co.montant), 0) FROM CashOperation co WHERE co.type = :type AND co.createdAt BETWEEN :start AND :end AND co.deleted = false")
    BigDecimal sumMontantByTypeAndDateBetween(@Param("type") CashOperationType type,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    // IMPROVEMENT: Soft delete filtering
    @Query("SELECT co FROM CashOperation co WHERE co.deleted = false")
    List<CashOperation> findAllActive();

    @Query("SELECT co FROM CashOperation co WHERE co.deleted = false")
    Page<CashOperation> findAllActive(Pageable pageable);

    @Query("SELECT co FROM CashOperation co WHERE co.type = :type AND co.deleted = false")
    List<CashOperation> findActiveByType(@Param("type") CashOperationType type);
}