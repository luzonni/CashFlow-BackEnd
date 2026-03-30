package com.luzonni.cashflow.features.auth.rest;

import com.luzonni.cashflow.features.auth.dto.RefreshRequest;
import com.luzonni.cashflow.features.auth.dto.TokenResponse;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.dto.LoginRequest;
import com.luzonni.cashflow.features.auth.service.AuthService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import io.quarkus.security.UnauthorizedException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.UUID;

@Path("/auth")
public class AuthResource {

    @Context
    private ContainerRequestContext context;

    private final AuthService authService;
    private final JsonWebToken jwt;

    @Inject
    public AuthResource(AuthService authService, JsonWebToken jwt) {
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
        try {
            TokenResponse login = authService.login(loginRequest, ip, userAgent);
            return Response.ok(login).build();
        }catch (UnauthorizedException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
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
        try {
            TokenResponse token = authService.register(requestRegister, ip, userAgent);
            return Response.ok(token).build();
        }catch (ConflictException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshToken(
            @Valid RefreshRequest refreshRequest
    ) {
        String ip = (String) context.getProperty("ip");
        String userAgent = (String) context.getProperty("userAgent");
        String refreshToken = refreshRequest.getRefreshToken();
        try {
            TokenResponse login = authService.refresh(refreshToken, ip, userAgent);
            return Response.ok(login).build();
        }catch (UnauthorizedException e) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("logout")
    public Response logout() {
        return Response.ok().build();
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
