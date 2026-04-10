package com.luzonni.cashflow.features.user.domain;

import com.luzonni.cashflow.features.authorization.domain.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    public Set<Role> roles = new HashSet<>();
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
