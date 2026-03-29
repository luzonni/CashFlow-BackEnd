package com.luzonni.cashflow.features.auth.repository;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RefreshTokenRepository implements PanacheRepository<RefreshToken> {

    public List<RefreshToken> findActiveByUserIdAndDeviceId(UUID userId, UUID deviceId) {
        return find(
                "user.id = ?1 and deviceId = ?2 and revoked = false",
                userId,
                deviceId
        ).list();
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
