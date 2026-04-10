package com.luzonni.cashflow.features.auth.service;

import com.luzonni.cashflow.features.auth.domain.RefreshToken;
import com.luzonni.cashflow.features.auth.dto.*;
import com.luzonni.cashflow.features.auth.mapper.AuthMapper;
import com.luzonni.cashflow.features.auth.repository.RefreshTokenRepository;
import com.luzonni.cashflow.features.authorization.domain.Role;
import com.luzonni.cashflow.features.authorization.repository.RoleRepository;
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
    private final RoleRepository roleRepository;

    private final static String FAKE_HASH;

    static {
        FAKE_HASH = HashUtils.hash("Imagine uma nova história para sua vida e acredite nela.");
    }

    @Inject
    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository repository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    public AuthResult authenticate(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getEmail());
        String hash = optionalUser
                .map(User::getPasswordHash)
                .orElse(FAKE_HASH);
        boolean valid = HashUtils.verify(loginRequest.getPassword(), hash);
        if(!valid || optionalUser.isEmpty()) {
            return AuthMapper.toAuthError(Response.Status.UNAUTHORIZED, "unauthorized");
        }
        User user = optionalUser.get();
        AuthCookies cookies = generateAndPersistTokens(user);
        return AuthMapper.toAuthResult(
                user,
                cookies
        );
    }

    @Transactional
    public AuthCookies generateAndPersistTokens(User user) {
        String accessToken = TokenUtils.generateAccessToken(user);
        String refreshToken = TokenUtils.generateRefreshToken();
        RefreshToken refreshTokenEntity = AuthMapper.toRefreshTokenEntity(
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
    public AuthResult register(RegisterRequest request) {
        User user = AuthMapper.toUserEntity(request);
        Role userRole = roleRepository.findByName("USER");
        user.getRoles().add(userRole);
        try {
            userRepository.persist(user);
            AuthCookies cookies = generateAndPersistTokens(user);
            return AuthMapper.toAuthResult(user, cookies);
        }catch (Exception e) {
            throw new ConflictException("Email or Username already exists");
        }
    }

    public AuthResult refresh(String refreshToken) {
        RefreshToken tl = repository.findByRefreshToken(refreshToken);
        if(tl == null) {
            return AuthMapper.toAuthError(Response.Status.FORBIDDEN, "refresh token not found");
        }
        if(tl.getRevoked()) {
            return AuthMapper.toAuthError(Response.Status.FORBIDDEN, "token has expired");
        }
        AuthCookies cookies = generateAndPersistTokens(tl.getUser());
        return AuthMapper.toAuthResult(null, cookies);
    }

    @Transactional
    public AuthCookies logout(String refreshToken) {
        if(refreshToken != null) {
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

    public User me(UUID userId) {
        return userRepository.getUserById(userId);
    }

}
