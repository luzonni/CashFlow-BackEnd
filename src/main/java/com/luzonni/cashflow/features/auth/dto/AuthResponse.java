package com.luzonni.cashflow.features.auth.dto;

import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.user.domain.User;

public record AuthResponse(User user, Settings settings, AuthCookies authCookies) {

}
