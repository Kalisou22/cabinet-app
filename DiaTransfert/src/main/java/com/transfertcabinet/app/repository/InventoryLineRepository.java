package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.InventoryLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InventoryLineRepository extends JpaRepository<InventoryLine, Long> {

    List<InventoryLine> findBySessionId(Long sessionId);

    @Query("SELECT COALESCE(SUM(il.montantMatin), 0) FROM InventoryLine il WHERE il.session.id = :sessionId")
    BigDecimal sumMontantMatinBySessionId(@Param("sessionId") Long sessionId);

    @Query("SELECT COALESCE(SUM(il.montantSoir), 0) FROM InventoryLine il WHERE il.session.id = :sessionId")
    BigDecimal sumMontantSoirBySessionId(@Param("sessionId") Long sessionId);

    // ✅ AJOUT : Récupérer les lignes actives d'une session (non supprimées)
    @Query("SELECT il FROM InventoryLine il WHERE il.session.id = :sessionId AND il.deleted = false")
    List<InventoryLine> findActiveBySessionId(@Param("sessionId") Long sessionId);
}