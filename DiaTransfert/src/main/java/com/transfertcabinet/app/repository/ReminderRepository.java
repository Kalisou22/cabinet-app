package com.transfertcabinet.app.repository;

import com.transfertcabinet.app.entity.Reminder;
import com.transfertcabinet.app.enums.ReminderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByStatus(ReminderStatus status);
    List<Reminder> findByDateRappelAndStatus(LocalDate dateRappel, ReminderStatus status);
    List<Reminder> findByClientId(Long clientId);
    List<Reminder> findByDateRappelBetween(LocalDate start, LocalDate end);
    List<Reminder> findByClientIdAndStatus(Long clientId, ReminderStatus status);

    @Query("SELECT r FROM Reminder r WHERE r.dateRappel = :date AND r.status = :status ORDER BY r.reminderTime")
    List<Reminder> findRemindersForDate(@Param("date") LocalDate date, @Param("status") ReminderStatus status);

    // IMPROVEMENT: Soft delete filtering
    @Query("SELECT r FROM Reminder r WHERE r.deleted = false")
    List<Reminder> findAllActive();

    @Query("SELECT r FROM Reminder r WHERE r.deleted = false")
    Page<Reminder> findAllActive(Pageable pageable);
}