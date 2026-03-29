package com.luzonni.cashflow.shared.util;

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

public class TokenUtils {


    @ConfigProperty(name = "auth.access-token.expiration-minutes")
    private static int accessTokenExpiration;

    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateAccessToken(UUID userId) {
        return Jwt.issuer("cashflow")
                .subject(userId.toString())
                .groups(Set.of("user"))
                .expiresIn(Duration.ofMinutes(accessTokenExpiration))
                .sign();
    }

    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}
