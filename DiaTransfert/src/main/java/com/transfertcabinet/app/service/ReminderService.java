package com.transfertcabinet.app.service;

import com.transfertcabinet.app.dto.request.ReminderRequest;
import com.transfertcabinet.app.dto.response.ReminderResponse;
import com.transfertcabinet.app.enums.ReminderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ReminderService {
    ReminderResponse create(ReminderRequest request);
    ReminderResponse update(Long id, ReminderRequest request);
    ReminderResponse findById(Long id);
    List<ReminderResponse> findAll();
    List<ReminderResponse> findByStatus(ReminderStatus status);
    List<ReminderResponse> findByDateRappel(LocalDate dateRappel);
    List<ReminderResponse> findByClientId(Long clientId);
    List<ReminderResponse> findPendingReminders();
    ReminderResponse markAsDone(Long id);
    void delete(Long id);

    // IMPROVEMENT: Pagination
    Page<ReminderResponse> findAllPaginated(Pageable pageable);
}