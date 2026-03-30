package com.luzonni.cashflow.features.auth.service;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.LoginRequest;
import com.luzonni.cashflow.features.auth.dto.RefreshRequest;
import com.luzonni.cashflow.features.auth.dto.TokenResponse;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.mapper.AuthMapper;
import com.luzonni.cashflow.features.auth.repository.RefreshTokenRepository;
import com.luzonni.cashflow.shared.util.TokenUtils;
import com.luzonni.cashflow.shared.util.HashUtils;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional
    public TokenResponse login(LoginRequest loginRequest, String ip, String userAgent) {
        User user = userRepository
                .findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if(!HashUtils.verify(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
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
        if (!activeTokens.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
                token.setRevokedAt(now);
                token.setReplacedByToken(refreshTokenEntity);
                repository.persist(token);
            }
        }
        repository.persist(refreshTokenEntity);
        return AuthMapper.toToken(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse register(RegisterRequest request, String ip, String userAgent) {
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
        String accessToken = TokenUtils.generateAccessToken(user.getId());
        String refreshToken = TokenUtils.generateRefreshToken();
        RefreshToken refreshTokenEntity = AuthMapper.toRefreshTokenEntity(
                user,
                refreshToken,
                ip,
                userAgent,
                refreshTokenExpiration
        );
        repository.persist(refreshTokenEntity);
        return AuthMapper.toToken(accessToken, refreshToken);
    }

    @Transactional
    public void deleteOldTokens() {
        repository.cleanupRevokedTokens();
    }

    @Transactional
    public TokenResponse refresh(String refreshToken, String ip, String userAgent) {
        RefreshToken tl = repository.findByToken(refreshToken);
        if(tl == null) {
            throw new UnauthorizedException("Invalid refresh token");
        }
        User user = tl.getUser();
        String newAccessToken = TokenUtils.generateAccessToken(user.getId());
        String newRefreshToken = TokenUtils.generateRefreshToken();
        RefreshToken newRefreshTokenEntity = AuthMapper.toRefreshTokenEntity(
                user,
                newRefreshToken,
                ip,
                userAgent,
                refreshTokenExpiration
        );
        List<RefreshToken> activeTokens = repository.findActiveByUserId(user.getId());
        if (!activeTokens.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            for (RefreshToken token : activeTokens) {
                token.setRevoked(true);
                token.setRevokedAt(now);
                token.setReplacedByToken(newRefreshTokenEntity);
                repository.persist(token);
            }
        }
        repository.persist(newRefreshTokenEntity);
        return AuthMapper.toToken(newAccessToken, newRefreshToken);
    }

    public User getUserById(UUID id) {
        return userRepository.getUserById(id);
    }
}
