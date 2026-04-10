package com.luzonni.cashflow.shared.util;

import jakarta.ws.rs.core.NewCookie;

public class CookieUtils {

    private static final boolean SECURE = false;

    public static NewCookie createAccessTokenCookie(String token) {
        return new NewCookie.Builder("accessToken")
                .value(token)
                .path("/")
                .maxAge(60 * 30)
                .httpOnly(true)
                .secure(SECURE)
                .build();
    }

    public static NewCookie createRefreshTokenCookie(String token) {
        return new NewCookie.Builder("refreshToken")
                .value(token)
                .path("/")
                .maxAge(60 * 60 * 24 * 15)
                .httpOnly(true)
                .secure(SECURE)
                .build();
    }

    public static NewCookie clearAccessTokenCookie() {
        return new NewCookie.Builder("accessToken")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(SECURE)
                .build();
    }

    public static NewCookie clearRefreshTokenCookie() {
        return new NewCookie.Builder("refreshToken")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(SECURE)
                .build();
    }

}
