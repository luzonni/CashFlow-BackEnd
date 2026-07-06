package com.luzonni.cashflow.features.auth.service;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.*;
import com.luzonni.cashflow.features.auth.repository.RefreshTokenRepository;
import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.settings.service.SettingsService;
import com.luzonni.cashflow.features.user.service.UserService;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.shared.util.CookieUtils;
import com.luzonni.cashflow.shared.util.TokenUtils;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "auth.refresh-token.expiration-days")
    int refreshTokenExpiration;

    private final UserRepository userRepository;
    private final RefreshTokenRepository repository;
    private final SettingsService settingsService;
    private final UserService userService;

    private final static String FAKE_HASH;

    static {
        FAKE_HASH = HashUtils.hash("Imagine uma nova história para sua vida e acredite nela.");
    }

    @Inject
    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository repository,
            SettingsService settingsService,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.settingsService = settingsService;
        this.userService = userService;
    }

    public AuthResponse authenticate(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        String hash = optionalUser
                .map(User::getPasswordHash)
                .orElse(FAKE_HASH);
        boolean valid = HashUtils.verify(loginRequest.getPassword(), hash);
        if (!valid || optionalUser.isEmpty()) {
            throw new AppException(Response.Status.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "unauthorized");
        }
        User user = optionalUser.get();
        Settings settings = settingsService.get(user.getId());
        AuthCookies cookies = generateAndPersistTokens(user);
        return new AuthResponse(user, settings, cookies);
    }

    @Transactional
    public AuthCookies generateAndPersistTokens(User user) {
        String accessToken = TokenUtils.generateAccessToken(user);
        String refreshToken = TokenUtils.generateRefreshToken();
        RefreshToken refreshTokenEntity = new RefreshToken(
                user,
                refreshToken,
                refreshTokenExpiration
        );
        List<RefreshToken> activeTokens = repository.findActiveByUserId(user.getId());
        if (!activeTokens.isEmpty()) { // TODO observar essa lógica!
            LocalDateTime now = LocalDateTime.now();
            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
                token.setRevokedAt(now);
                token.setReplacedByToken(refreshTokenEntity);
                repository.persist(token);
            }
        }
        var accessCookie = CookieUtils.createAccessTokenCookie(accessToken);
        var refreshTokenCookie = CookieUtils.createRefreshTokenCookie(refreshToken);
        AuthCookies authCookies = new AuthCookies(accessCookie, refreshTokenCookie);
        repository.persist(refreshTokenEntity);
        return authCookies;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = userService.create(
                request.getUsername(),
                request.getEmail(),
                request.getBirthday(),
                request.getPassword()
        );
        Settings settings = settingsService.get(user.getId());
        AuthCookies cookies = generateAndPersistTokens(user);
        return new AuthResponse(user, settings, cookies);
    }

    public AuthCookies refresh(String refreshToken) {
        RefreshToken tl = repository.findByRefreshToken(refreshToken);
        if (tl == null) {
            throw new AppException(Response.Status.FORBIDDEN, ErrorCode.REFRESH_TOKEN_INVALID, "refresh token not found");
        }
        if (tl.getRevoked()) {
            throw new AppException(Response.Status.FORBIDDEN, ErrorCode.REFRESH_TOKEN_EXPIRED, "token has expired");
        }
        return generateAndPersistTokens(tl.getUser());
    }

    @Transactional
    public AuthCookies logout(String refreshToken) {
        if (refreshToken != null) {
            RefreshToken tl = repository.findByRefreshToken(refreshToken);
            if (tl != null) {
                tl.revoke(null);
                repository.persist(tl);
            }
        }
        var accessCookie = CookieUtils.clearAccessTokenCookie();
        var refreshTokenCookie = CookieUtils.clearRefreshTokenCookie();
        return new AuthCookies(accessCookie, refreshTokenCookie);
    }

    @Transactional
    public void deleteOldTokens() {
        repository.cleanupRevokedTokens();
    }

    public UserResponse me(UUID userId) {
        Settings settings = settingsService.get(userId);
        return new UserResponse(userRepository.getUserById(userId), settings);
    }

}
