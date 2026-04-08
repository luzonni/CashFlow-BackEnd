package com.luzonni.cashflow.features.auth.domain;

import com.luzonni.cashflow.features.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @UuidGenerator()
    private UUID id;
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "token_hash")
    private String tokenHash;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    @Column
    private Boolean revoked = false;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;
    @ManyToOne()
    @JoinColumn(name = "replaced_by_token_id")
    private RefreshToken replacedByToken;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void setExpiry(int days) {
        this.expiresAt = LocalDateTime.now().plusDays(days);
    }

    public void revoke(RefreshToken newRefreshToken) {
        this.revoked = true;
        this.revokedAt = LocalDateTime.now();
        this.replacedByToken =  newRefreshToken;
    }

    public void replace(RefreshToken newToken) {
        this.replacedByToken = newToken;
    }

}
