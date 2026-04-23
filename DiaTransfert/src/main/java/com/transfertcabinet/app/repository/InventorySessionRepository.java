package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.InventorySession;
import com.transfertcabinet.app.enums.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventorySessionRepository extends JpaRepository<InventorySession, Long> {

    // ✅ CORRIGÉ : Ne retourne que les sessions actives (non supprimées)
    @Query("SELECT s FROM InventorySession s WHERE s.date = :date AND s.deleted = false")
    Optional<InventorySession> findByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM InventorySession s WHERE s.date = :date AND s.status = :status AND s.deleted = false")
    Optional<InventorySession> findByDateAndStatus(@Param("date") LocalDate date, @Param("status") InventoryStatus status);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM InventorySession s WHERE s.date = :date AND s.deleted = false")
    boolean existsByDate(@Param("date") LocalDate date);

    @Query("SELECT s FROM InventorySession s WHERE s.deleted = false")
    List<InventorySession> findAllActive();

    @Query("SELECT s FROM InventorySession s WHERE s.deleted = false")
    Page<InventorySession> findAllActive(Pageable pageable);

    @Query("SELECT s FROM InventorySession s WHERE s.status = :status AND s.deleted = false")
    List<InventorySession> findByStatus(@Param("status") InventoryStatus status);
}