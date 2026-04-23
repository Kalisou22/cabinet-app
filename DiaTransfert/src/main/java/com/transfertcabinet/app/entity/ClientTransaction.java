package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.transfertcabinet.app.enums.TransactionType;
import com.transfertcabinet.app.enums.TransactionNature;
import com.transfertcabinet.app.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "client_transactions",
        indexes = {
                @Index(name = "idx_transaction_client_id", columnList = "client_id"),
                @Index(name = "idx_transaction_status", columnList = "status"),
                @Index(name = "idx_transaction_nature", columnList = "nature"),
                @Index(name = "idx_transaction_due_date", columnList = "due_date")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientTransaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_client"))
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionNature nature;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(name = "reste_a_payer", precision = 19, scale = 4)
    private BigDecimal resteAPayer;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_user"))
    private User user;

    // Relation inverse pour Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private Client clientRelation;

    @PrePersist
    @PreUpdate
    protected void validateAndSetDefaults() {
        super.onCreate();

        if (status == null) {
            status = TransactionStatus.EN_COURS;
        }

        // FIX CRITIQUE : Cash transaction
        if (nature == TransactionNature.CASH) {
            this.resteAPayer = BigDecimal.ZERO;
            this.dueDate = null;
            this.status = TransactionStatus.REMBOURSE;
        }

        // CREDIT transaction
        if (nature == TransactionNature.CREDIT) {
            if (resteAPayer == null) {
                this.resteAPayer = this.montant;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientTransaction that = (ClientTransaction) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClientTransaction{" +
                "id=" + id +
                ", type=" + type +
                ", nature=" + nature +
                ", montant=" + montant +
                ", resteAPayer=" + resteAPayer +
                ", status=" + status +
                '}';
    }
}