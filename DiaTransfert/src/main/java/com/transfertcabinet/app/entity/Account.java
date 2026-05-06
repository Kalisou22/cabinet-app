package com.transfertcabinet.app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 30)
    private String type;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (active == null) active = true;
    }
}