package io.luzonni.mindbudget.util;

import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

public class JwtUtil {

    public static String generateToken(UUID userId, String email) {
        return Jwt.issuer("mindbudget")
                .subject(userId.toString())
                .claim("email", email)
                .groups(Set.of("user"))
                .expiresIn(Duration.ofHours(2))
                .sign();
    }

}
