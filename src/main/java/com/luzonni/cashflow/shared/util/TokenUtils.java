package com.luzonni.cashflow.shared.util;

import com.luzonni.cashflow.features.authorization.domain.Role;
import com.luzonni.cashflow.features.user.domain.User;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class TokenUtils {


    @ConfigProperty(name = "auth.access-token.expiration-minutes")
    private static long accessTokenExpiration;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateAccessToken(User user) {
        UUID userId = user.getId();
        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        long now = System.currentTimeMillis() / 1000;
        return Jwt.issuer("cashflow")
                .subject(userId.toString())
                .groups(roles)
                .issuedAt(now)
                .expiresIn(now + 60L * accessTokenExpiration)
                .sign();
    }

    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}
