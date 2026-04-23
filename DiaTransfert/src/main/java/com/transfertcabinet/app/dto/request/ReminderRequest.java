package com.transfertcabinet.app.dto.request;

import com.transfertcabinet.app.enums.ReminderStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequest {

    @NotNull(message = "L'ID du client est obligatoire")
    private Long clientId;

    @NotBlank(message = "Le message est obligatoire")
    @Size(max = 500, message = "Le message ne doit pas dépasser 500 caractères")
    private String message;

    @NotNull(message = "La date de rappel est obligatoire")
    @FutureOrPresent(message = "La date de rappel doit être dans le présent ou le futur")
    private LocalDate dateRappel;

    private LocalTime reminderTime;

    private ReminderStatus status;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;
}