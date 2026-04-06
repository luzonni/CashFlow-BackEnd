package com.luzonni.cashflow.features.auth.dto;

import jakarta.ws.rs.core.NewCookie;
import lombok.Data;

@Data
public class AuthCookies {

    private NewCookie accessToken;
    private NewCookie refreshToken;

    public AuthCookies(
            NewCookie accessToken,
            NewCookie refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
