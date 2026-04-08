package com.luzonni.cashflow.features.auth.mapper;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.AuthCookies;
import com.luzonni.cashflow.features.auth.dto.AuthResult;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import com.luzonni.cashflow.shared.dto.ErrorResponse;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;
import jakarta.ws.rs.core.Response;

public class AuthMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(user);
    }

    public static User toUserEntity(RegisterRequest request) {
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
            int daysLeft
    ) {
        RefreshToken entity = new RefreshToken();
        String hashedRefreshToken = HashUtils.sha256(refreshToken);
        entity.setTokenHash(hashedRefreshToken);
        entity.setRevoked(false);
        entity.setUser(user);
        entity.setExpiry(daysLeft);
        return entity;
    }

    public static AuthResult toAuthResult(User user, AuthCookies cookies) {
        return new AuthResult(user, cookies);
    }

    public static AuthResult toAuthError(Response.Status status, String message) {
        AuthResult authResult = new AuthResult(null, null);
        authResult.setError(new ErrorResponse(status, message));
        return authResult;
    }

}
