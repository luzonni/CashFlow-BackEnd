package com.luzonni.cashflow.features.auth.rest;

import com.luzonni.cashflow.features.auth.dto.*;
import com.luzonni.cashflow.features.auth.mapper.AuthMapper;
import com.luzonni.cashflow.features.auth.service.AuthService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Map;
import java.util.UUID;

@Path("/auth")
public class AuthResource {

    @Context
    private ContainerRequestContext context;

    private final AuthService authService;
    private final JsonWebToken jwt;

    @Inject
    public AuthResource(
            AuthService authService,
            JsonWebToken jwt
    ) {
        this.authService = authService;
        this.jwt = jwt;
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(
            @Valid LoginRequest loginRequest
    ) {
        String ip = (String) context.getProperty("ip");
        String userAgent = (String) context.getProperty("userAgent");
        User user = authService.authenticate(loginRequest);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        AuthCookies cookies = authService.GenerateAndPersistTokens(user, ip, userAgent);
        return Response
                .ok(AuthMapper.toAuthResponse(user, true))
                .cookie(cookies.getAccessToken())
                .cookie(cookies.getRefreshToken())
                .build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @Valid RegisterRequest requestRegister
    ) {
        String ip = (String) context.getProperty("ip");
        String userAgent = (String) context.getProperty("userAgent");
        User user = authService.register(requestRegister);
        AuthCookies cookies = authService.GenerateAndPersistTokens(user, ip, userAgent);
        return Response
                .ok(user)
                .cookie(cookies.getAccessToken())
                .cookie(cookies.getRefreshToken())
                .entity(cookies.getRefreshToken())
                .build();
    }

    @POST
    @Path("refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(
            @Valid RefreshTokenRequest refreshRequest
    ) {
        String ip = (String) context.getProperty("ip");
        String userAgent = (String) context.getProperty("userAgent");
        String refreshToken = refreshRequest.getRefreshToken();
        User user = authService.refresh(refreshToken);
        AuthCookies cookies = authService.GenerateAndPersistTokens(user, ip, userAgent);
        return Response
                .ok(user)
                .cookie(cookies.getAccessToken())
                .cookie(cookies.getRefreshToken())
                .build();
    }

    @POST
    @Path("logout")
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(
            @Valid RefreshTokenRequest refreshRequest
    ) {
        String refreshToken = refreshRequest.getRefreshToken();
        AuthCookies logout = authService.logout(refreshToken);
        return Response
                .noContent()
                .cookie(logout.getAccessToken())
                .cookie(logout.getRefreshToken())
                .build();
    }

    @GET
    @Path("me")
    @RolesAllowed("user")
    public Response me() {
        UUID userId = UUID.fromString(jwt.getSubject());
        return Response
                .ok(UserResponse.from(authService.getUserById(userId)))
                .build();
    }

}
