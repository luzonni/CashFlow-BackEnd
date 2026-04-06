package com.luzonni.cashflow.features.auth.mapper;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.dto.AuthResponse;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;

public class AuthMapper {

    public static AuthResponse toAuthResponse(User user, boolean success) {
        AuthResponse loginResponse = new AuthResponse();
        loginResponse.setUser(user);
        loginResponse.setSuccess(success);
        return loginResponse;
    }

    public static User toEntity(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setBirthday(request.getBirthday());
        String hash = HashUtils.hash(request.getPassword());
        user.setPasswordHash(hash);
        return user;
    }


    public static RefreshToken toRefreshTokenEntity(
            User user,
            String refreshToken,
            String ip,
            String userAgent,
            int daysLeft
    ) {
        RefreshToken entity = new RefreshToken();
        String hashedRefreshToken = HashUtils.sha256(refreshToken);
        entity.setTokenHash(hashedRefreshToken);
        entity.setRevoked(false);
        entity.setUser(user);
        entity.setDeviceInfo(userAgent);
        entity.setIpAddress(ip);
        entity.setExpiry(daysLeft);
        return entity;
    }


}
