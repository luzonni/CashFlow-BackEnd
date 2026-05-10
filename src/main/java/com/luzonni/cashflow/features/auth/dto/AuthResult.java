package com.luzonni.cashflow.features.auth.dto;

import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.shared.dto.ErrorResponse;
import lombok.Data;

@Data
public class AuthResult {

    private ErrorResponse error;
    private User user;
    private Settings settings;
    private AuthCookies authCookies;

    public AuthResult(User user, Settings settings, AuthCookies authCookies) {
        this.user = user;
        this.settings = settings;
        this.authCookies = authCookies;
    }

    public boolean isFailure() {
        return error != null;
    }

}
