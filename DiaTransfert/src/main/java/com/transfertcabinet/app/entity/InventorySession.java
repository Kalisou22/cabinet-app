package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.transfertcabinet.app.enums.InventoryStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "inventory_sessions",
        uniqueConstraints = {
                // ✅ CORRIGÉ : Inclure deleted dans la contrainte d'unicité
                @UniqueConstraint(name = "uk_inventory_session_date", columnNames = {"date", "deleted"})
        },
        indexes = {
                @Index(name = "idx_inventory_session_status", columnList = "status"),
                @Index(name = "idx_inventory_session_date", columnList = "date")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(precision = 19, scale = 4)
    private BigDecimal resultat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_session_user"))
    private User user;

    // ✅ NOUVEAU : Justification pour la fermeture
    @Column(length = 500)
    private String justification;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = InventoryStatus.OPEN;
        }
        if (resultat == null) {
            resultat = BigDecimal.ZERO;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventorySession that = (InventorySession) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InventorySession{" +
                "id=" + id +
                ", date=" + date +
                ", resultat=" + resultat +
                ", status=" + status +
                '}';
    }
}