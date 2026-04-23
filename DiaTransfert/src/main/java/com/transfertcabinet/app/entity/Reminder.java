package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.transfertcabinet.app.enums.ReminderStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "reminders",
        indexes = {
                @Index(name = "idx_reminder_client_id", columnList = "client_id"),
                @Index(name = "idx_reminder_status", columnList = "status"),
                @Index(name = "idx_reminder_date_rappel", columnList = "date_rappel")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reminder extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reminder_client"))
    private Client client;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "date_rappel", nullable = false)
    private LocalDate dateRappel;

    @Column(name = "reminder_time")
    private LocalTime reminderTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReminderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reminder_user"))
    private User user;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = ReminderStatus.PENDING;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return id != null && Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", dateRappel=" + dateRappel +
                ", reminderTime=" + reminderTime +
                ", status=" + status +
                '}';
    }
}