package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "inventory_lines",
        indexes = {
                @Index(name = "idx_inventory_line_session_id", columnList = "session_id")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_line_session"))
    private InventorySession session;

    @Column(name = "platform_name", nullable = false, length = 50)
    private String platformName;

    @Column(name = "montant_matin", precision = 19, scale = 4)
    private BigDecimal montantMatin;

    @Column(name = "montant_soir", precision = 19, scale = 4)
    private BigDecimal montantSoir;

    // SOFT DELETE pour InventoryLine
    @Column(name = "deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @PrePersist
    @PreUpdate
    protected void initializeMontants() {
        if (montantMatin == null) {
            montantMatin = BigDecimal.ZERO;
        }
        if (montantSoir == null) {
            montantSoir = BigDecimal.ZERO;
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryLine that = (InventoryLine) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "InventoryLine{" +
                "id=" + id +
                ", platformName='" + platformName + '\'' +
                ", montantMatin=" + montantMatin +
                ", montantSoir=" + montantSoir +
                '}';
    }
}