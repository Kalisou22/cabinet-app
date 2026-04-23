package com.transfertcabinet.app.dto.response;

import com.transfertcabinet.app.enums.ReminderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponse {
    private Long id;
    private Long clientId;
    private String clientNom;
    private String clientTelephone;
    private String message;
    private LocalDate dateRappel;
    private LocalTime reminderTime;
    private ReminderStatus status;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}