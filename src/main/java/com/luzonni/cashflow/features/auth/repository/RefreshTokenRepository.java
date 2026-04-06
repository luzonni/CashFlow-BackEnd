package com.luzonni.cashflow.features.auth.repository;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.shared.util.HashUtils;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepository<RefreshToken> {

    public List<RefreshToken> findActiveByUserId(UUID userId) {
        return find(
                "user.id = ?1 and revoked = false",
                userId
        ).list();
    }

    public RefreshToken findByRefreshToken(String token) {
        String hashedToken = HashUtils.sha256(token);
        return find("tokenHash = ?1", hashedToken).firstResult();
    }

    @Transactional
    public void cleanupRevokedTokens() {
        delete(
                "revoked = true and expiresAt < ?1 and revokedAt < ?2",
                LocalDateTime.now(),
                LocalDateTime.now().minusDays(7)
        );
    }

}
