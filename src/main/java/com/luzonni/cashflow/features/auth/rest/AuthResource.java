package com.luzonni.cashflow.features.auth.rest;

import com.luzonni.cashflow.features.auth.dto.*;
import com.luzonni.cashflow.features.auth.mapper.AuthMapper;
import com.luzonni.cashflow.features.auth.service.AuthService;
import com.luzonni.cashflow.features.user.domain.User;
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
        AuthResult result = authService.authenticate(loginRequest);
        if (result.isFailure()) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity(result.getError())
                    .build();
        }
        return Response
                .ok(AuthMapper.toUserResponse(result.getUser()))
                .cookie(result.getAuthCookies().getAccessToken())
                .cookie(result.getAuthCookies().getRefreshToken())
                .build();
    }

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(
            @Valid RegisterRequest requestRegister
    ) {
        AuthResult result = authService.register(requestRegister);
        if(result.isFailure()) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(result.getError())
                    .build();
        }
        return Response
                .ok(AuthMapper.toUserResponse(result.getUser()))
                .cookie(result.getAuthCookies().getAccessToken())
                .cookie(result.getAuthCookies().getRefreshToken())
                .build();
    }

    @POST
    @Path("refresh")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response refreshToken(
            @CookieParam("refreshToken") String refreshToken
    ) {
        AuthResult result = authService.refresh(refreshToken);
        if(result.isFailure()) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity(result.getError())
                    .build();
        }
        return Response
                .noContent()
                .cookie(result.getAuthCookies().getAccessToken())
                .cookie(result.getAuthCookies().getRefreshToken())
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
        User user = authService.me(userId);
        return Response
                .ok(AuthMapper.toUserResponse(user))
                .build();
    }

}
