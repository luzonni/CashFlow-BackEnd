package com.luzonni.cashflow.features.user.domain;

import com.luzonni.cashflow.shared.util.HashUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
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
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    @Column(name = "verification_token")
    private String verificationToken;
    @Column(name = "verification_token_expires_at")
    private LocalDateTime verificationTokenExpiresAt;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public User() { }

    public User(
            String username,
            String email,
            LocalDate birthday,
            String password
    ) {
        this.username = username;
        this.email = email;
        this.birthday = birthday;
        this.passwordHash = HashUtils.hash(password);
        this.emailVerified = false;
        createVerificationToken();
    }

    public void createVerificationToken() {
        this.verificationToken = UUID.randomUUID().toString();
        this.verificationTokenExpiresAt = LocalDateTime.now().plusHours(24);
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
