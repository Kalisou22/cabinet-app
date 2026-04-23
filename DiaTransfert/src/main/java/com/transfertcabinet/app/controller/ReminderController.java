package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.dto.request.ReminderRequest;
import com.transfertcabinet.app.dto.response.ReminderResponse;
import com.transfertcabinet.app.enums.ReminderStatus;
import com.transfertcabinet.app.service.ReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public ResponseEntity<ReminderResponse> createReminder(
            @Valid @RequestBody ReminderRequest request) {
        log.info("REST request to create Reminder for client: {}", request.getClientId());
        ReminderResponse response = reminderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long id, @Valid @RequestBody ReminderRequest request) {
        log.info("REST request to update Reminder: {}", id);
        ReminderResponse response = reminderService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderResponse> getReminder(@PathVariable Long id) {
        log.info("REST request to get Reminder: {}", id);
        ReminderResponse response = reminderService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders(
            @RequestParam(required = false) ReminderStatus status) {
        log.info("REST request to get Reminders, status: {}", status);
        List<ReminderResponse> responses = status != null ?
                reminderService.findByStatus(status) : reminderService.findAll();
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Pagination
    @GetMapping("/paginated")
    public ResponseEntity<Page<ReminderResponse>> getAllRemindersPaginated(Pageable pageable) {
        log.info("REST request to get paginated Reminders");
        Page<ReminderResponse> responses = reminderService.findAllPaginated(pageable);
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Rappels en attente (métier)
    @GetMapping("/pending")
    public ResponseEntity<List<ReminderResponse>> getPendingReminders() {
        log.info("REST request to get all pending Reminders");
        List<ReminderResponse> responses = reminderService.findPendingReminders();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReminderResponse>> getRemindersByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("REST request to get Reminders for date: {}", date);
        List<ReminderResponse> responses = reminderService.findByDateRappel(date);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<ReminderResponse>> getRemindersByClient(
            @PathVariable Long clientId) {
        log.info("REST request to get Reminders for client: {}", clientId);
        List<ReminderResponse> responses = reminderService.findByClientId(clientId);
        return ResponseEntity.ok(responses);
    }

    // IMPROVEMENT: Rappels du jour
    @GetMapping("/today")
    public ResponseEntity<List<ReminderResponse>> getTodayReminders() {
        log.info("REST request to get today's Reminders");
        List<ReminderResponse> responses = reminderService.findByDateRappel(LocalDate.now());
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<ReminderResponse> markReminderAsDone(@PathVariable Long id) {
        log.info("REST request to mark Reminder as done: {}", id);
        ReminderResponse response = reminderService.markAsDone(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        log.info("REST request to delete Reminder: {}", id);
        reminderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}