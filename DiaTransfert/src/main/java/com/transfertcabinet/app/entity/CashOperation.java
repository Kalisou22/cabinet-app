package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.transfertcabinet.app.enums.CashOperationType;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cash_operations",
        indexes = {
                @Index(name = "idx_cash_operation_type", columnList = "type"),
                @Index(name = "idx_cash_operation_created_at", columnList = "created_at"),
                @Index(name = "idx_cash_operation_category_id", columnList = "category_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashOperation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_operation_category"))
    private OperationCategory category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CashOperationType type;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_cash_operation_user"))
    private User user;

    @PrePersist
    @PreUpdate
    protected void validateMontant() {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être strictement positif");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashOperation that = (CashOperation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CashOperation{" +
                "id=" + id +
                ", montant=" + montant +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}