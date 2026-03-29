package com.luzonni.cashflow.features.user.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @UuidGenerator()
    private UUID id;
    @Column
    private String username;
    @Column
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column
    private LocalDate birthday;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
