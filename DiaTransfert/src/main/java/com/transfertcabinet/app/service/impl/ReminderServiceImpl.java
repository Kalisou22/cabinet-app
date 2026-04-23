package com.transfertcabinet.app.service.impl;

import com.transfertcabinet.app.dto.request.ReminderRequest;
import com.transfertcabinet.app.dto.response.ReminderResponse;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Reminder;
import com.transfertcabinet.app.entity.User;
import com.transfertcabinet.app.enums.ReminderStatus;
import com.transfertcabinet.app.exception.BusinessException;
import com.transfertcabinet.app.exception.ResourceNotFoundException;
import com.transfertcabinet.app.mapper.ReminderMapper;
import com.transfertcabinet.app.repository.ClientRepository;
import com.transfertcabinet.app.repository.ReminderRepository;
import com.transfertcabinet.app.repository.UserRepository;
import com.transfertcabinet.app.service.ReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ReminderMapper reminderMapper;

    @Override
    public ReminderResponse create(ReminderRequest request) {
        log.info("Creating new reminder for client ID: {}", request.getClientId());

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Reminder reminder = reminderMapper.toEntity(request, client, user);
        Reminder savedReminder = reminderRepository.save(reminder);

        log.info("Reminder created successfully with ID: {}", savedReminder.getId());
        return reminderMapper.toResponse(savedReminder);
    }

    @Override
    public ReminderResponse update(Long id, ReminderRequest request) {
        log.info("Updating reminder with ID: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        Client client = request.getClientId() != null ?
                clientRepository.findById(request.getClientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId())) :
                reminder.getClient();

        User user = request.getUserId() != null ?
                userRepository.findById(request.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId())) :
                reminder.getUser();

        reminderMapper.updateEntity(reminder, request, client, user);
        Reminder updatedReminder = reminderRepository.save(reminder);

        log.info("Reminder updated successfully with ID: {}", updatedReminder.getId());
        return reminderMapper.toResponse(updatedReminder);
    }

    @Override
    @Transactional(readOnly = true)
    public ReminderResponse findById(Long id) {
        log.debug("Finding reminder by ID: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        return reminderMapper.toResponse(reminder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> findAll() {
        log.debug("Finding all reminders");

        return reminderRepository.findAllActive().stream()
                .map(reminderMapper::toResponse)
                .collect(Collectors.toList());
    }

    // IMPROVEMENT: Pagination
    @Override
    @Transactional(readOnly = true)
    public Page<ReminderResponse> findAllPaginated(Pageable pageable) {
        log.debug("Finding all paginated reminders");
        return reminderRepository.findAllActive(pageable)
                .map(reminderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> findByStatus(ReminderStatus status) {
        log.debug("Finding reminders by status: {}", status);

        return reminderRepository.findByStatus(status).stream()
                .filter(r -> !r.getDeleted())
                .map(reminderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> findByDateRappel(LocalDate dateRappel) {
        log.debug("Finding reminders for date: {}", dateRappel);

        return reminderRepository.findRemindersForDate(dateRappel, ReminderStatus.PENDING).stream()
                .filter(r -> !r.getDeleted())
                .map(reminderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> findByClientId(Long clientId) {
        log.debug("Finding reminders for client ID: {}", clientId);

        return reminderRepository.findByClientId(clientId).stream()
                .filter(r -> !r.getDeleted())
                .map(reminderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReminderResponse> findPendingReminders() {
        log.debug("Finding all pending reminders");

        return reminderRepository.findByStatus(ReminderStatus.PENDING).stream()
                .filter(r -> !r.getDeleted())
                .map(reminderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReminderResponse markAsDone(Long id) {
        log.info("Marking reminder as done with ID: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        if (reminder.getStatus() == ReminderStatus.DONE) {
            throw new BusinessException("Reminder is already marked as done");
        }

        reminder.setStatus(ReminderStatus.DONE);
        Reminder updatedReminder = reminderRepository.save(reminder);

        log.info("Reminder marked as done successfully with ID: {}", id);
        return reminderMapper.toResponse(updatedReminder);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting reminder with ID: {}", id);

        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", id));

        // IMPROVEMENT: soft delete
        reminder.setDeleted(true);
        reminderRepository.save(reminder);

        log.info("Reminder soft deleted successfully with ID: {}", id);
    }
}