package com.transfertcabinet.app.mapper;

import com.transfertcabinet.app.dto.request.ReminderRequest;
import com.transfertcabinet.app.dto.response.ReminderResponse;
import com.transfertcabinet.app.entity.Client;
import com.transfertcabinet.app.entity.Reminder;
import com.transfertcabinet.app.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ReminderMapper {

    public Reminder toEntity(ReminderRequest request, Client client, User user) {
        if (request == null) {
            return null;
        }

        return Reminder.builder()
                .client(client)
                .message(request.getMessage())
                .dateRappel(request.getDateRappel())
                .reminderTime(request.getReminderTime())
                .status(request.getStatus())
                .user(user)
                .build();
    }

    public ReminderResponse toResponse(Reminder reminder) {
        if (reminder == null) {
            return null;
        }

        return ReminderResponse.builder()
                .id(reminder.getId())
                .clientId(reminder.getClient() != null ? reminder.getClient().getId() : null)
                .clientNom(reminder.getClient() != null ? reminder.getClient().getNom() : null)
                .clientTelephone(reminder.getClient() != null ? reminder.getClient().getTelephone() : null)
                .message(reminder.getMessage())
                .dateRappel(reminder.getDateRappel())
                .reminderTime(reminder.getReminderTime())
                .status(reminder.getStatus())
                .userId(reminder.getUser() != null ? reminder.getUser().getId() : null)
                .username(reminder.getUser() != null ? reminder.getUser().getUsername() : null)
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }

    public void updateEntity(Reminder reminder, ReminderRequest request, Client client, User user) {
        if (request.getMessage() != null) {
            reminder.setMessage(request.getMessage());
        }
        if (request.getDateRappel() != null) {
            reminder.setDateRappel(request.getDateRappel());
        }
        if (request.getReminderTime() != null) {
            reminder.setReminderTime(request.getReminderTime());
        }
        if (request.getStatus() != null) {
            reminder.setStatus(request.getStatus());
        }
        if (client != null) {
            reminder.setClient(client);
        }
        if (user != null) {
            reminder.setUser(user);
        }
    }
}