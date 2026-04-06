package com.luzonni.cashflow.features.auth.service;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.AuthCookies;
import com.luzonni.cashflow.features.auth.dto.LoginRequest;
import com.luzonni.cashflow.features.auth.dto.AuthResponse;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.mapper.AuthMapper;
import com.luzonni.cashflow.features.auth.repository.RefreshTokenRepository;
import com.luzonni.cashflow.shared.util.CookieUtils;
import com.luzonni.cashflow.shared.util.TokenUtils;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class AuthService {

    @ConfigProperty(name = "auth.refresh-token.expiration-days")
    int refreshTokenExpiration;

    private final UserRepository userRepository;
    private final RefreshTokenRepository repository;

    @Inject
    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository repository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
    }

    public User authenticate(LoginRequest loginRequest) {
        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if(!HashUtils.verify(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        return user;
    }

    @Transactional
    public AuthCookies GenerateAndPersistTokens(User user, String ip, String userAgent) {
        String accessToken = TokenUtils.generateAccessToken(user.getId());
        String refreshToken = TokenUtils.generateRefreshToken();
        RefreshToken refreshTokenEntity = AuthMapper.toRefreshTokenEntity(
                user,
                refreshToken,
                ip,
                userAgent,
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
    public User register(RegisterRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        if(byEmail.isPresent()) {
            throw new ConflictException("Email already exists");
        }
        Optional<User> byUsername = userRepository.findByUsername(request.getUsername());
        if(byUsername.isPresent()) {
            throw new ConflictException("Username already exists");
        }
        User user = AuthMapper.toEntity(request);
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public void deleteOldTokens() {
        repository.cleanupRevokedTokens();
    }

    @Transactional
    public User refresh(String refreshToken) {
        RefreshToken tl = repository.findByRefreshToken(refreshToken);
        if(tl == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        if(tl.getRevoked()) {
            throw new UnauthorizedException("Token revoked");
        }
        return tl.getUser();
    }

    @Transactional
    public AuthCookies logout(String refreshToken) {
        RefreshToken tl = repository.findByRefreshToken(refreshToken);
        if(tl == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        tl.revoke(null);
        repository.persist(tl);
        var accessCookie = CookieUtils.clearAccessTokenCookie();
        var refreshTokenCookie = CookieUtils.clearRefreshTokenCookie();
        return new AuthCookies(accessCookie, refreshTokenCookie);
    }

    public User getUserById(UUID id) {
        return userRepository.getUserById(id);
    }
}
