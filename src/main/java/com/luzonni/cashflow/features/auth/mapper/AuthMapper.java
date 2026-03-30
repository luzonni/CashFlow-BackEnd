package com.luzonni.cashflow.features.auth.mapper;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.dto.TokenResponse;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;

import java.util.UUID;

public class AuthMapper {

    public static TokenResponse toToken(String accessToken, String refreshToken) {
        TokenResponse loginResponse = new TokenResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setType("Bearer");
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
