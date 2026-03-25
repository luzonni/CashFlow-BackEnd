package io.luzonni.mindbudget.util;

import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

public class JwtUtil {

    public static String generateToken(UUID userId) {
        return Jwt.issuer("mindbudget")
                .subject(userId.toString())
                .groups(Set.of("user"))
                .expiresIn(Duration.ofDays(7))
                .sign();
    }

}
