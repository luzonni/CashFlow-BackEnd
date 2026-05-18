package com.luzonni.cashflow.features.auth.rest;

import com.luzonni.cashflow.features.auth.dto.*;
import com.luzonni.cashflow.features.auth.service.AuthService;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("/auth")
public class AuthResource {

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
        AuthResponse result = authService.authenticate(loginRequest);
        return Response
                .ok(new UserResponse(result.user(), result.settings()))
                .cookie(result.authCookies().getAccessToken())
                .cookie(result.authCookies().getRefreshToken())
                .build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @Valid RegisterRequest requestRegister
    ) {
        AuthResponse result = authService.register(requestRegister);
        return Response
                .ok(new UserResponse(result.user(), result.settings()))
                .cookie(result.authCookies().getAccessToken())
                .cookie(result.authCookies().getRefreshToken())
                .build();
    }

    @POST
    @Path("refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshToken(
            @CookieParam("refreshToken") String refreshToken
    ) {
        AuthCookies result = authService.refresh(refreshToken);
        return Response
                .noContent()
                .cookie(result.getAccessToken())
                .cookie(result.getRefreshToken())
                .build();
    }

    @POST
    @Path("logout")
    public Response logout(
            @CookieParam("refreshToken") String refreshToken
    ) {
        AuthCookies logout = authService.logout(refreshToken);
        return Response
                .noContent()
                .cookie(logout.getAccessToken())
                .cookie(logout.getRefreshToken())
                .build();
    }

    @GET
    @Path("me")
    @RolesAllowed("USER")
    public Response me() {
        UUID userId = UUID.fromString(jwt.getSubject());
        UserResponse user = authService.me(userId);
        return Response
                .ok(user)
                .build();
    }

}
