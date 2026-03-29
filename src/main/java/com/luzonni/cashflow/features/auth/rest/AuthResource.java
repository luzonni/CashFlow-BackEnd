package com.luzonni.cashflow.features.auth.rest;

import com.luzonni.cashflow.features.auth.dto.TokenResponse;
import com.luzonni.cashflow.features.auth.dto.RegisterRequest;
import com.luzonni.cashflow.features.auth.dto.LoginRequest;
import com.luzonni.cashflow.features.auth.service.AuthService;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import io.quarkus.security.UnauthorizedException;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/auth")
public class AuthResource {

    @Context
    private ContainerRequestContext context;

    private final AuthService authService;

    @Inject
    public AuthResource(AuthService authService) {
        this.authService = authService;
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
        UUID deviceId = loginRequest.getDeviceId();
        try {
            TokenResponse login = authService.login(loginRequest, ip, userAgent, deviceId);
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
        UUID deviceId = requestRegister.getDeviceId();
        try {
            TokenResponse token = authService.register(requestRegister, ip, userAgent, deviceId);
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
    public Response refreshToken(String token) {
        //Toda vez que eu dar refresh eu preciso desvalidar o antigo e gerar um novo!
        /*
        Como revogar? = todos os refreshtokens são armazenados no banco de dados, quando entrar
        no refresh, ele verifica no banco se ele existe ( whitelist )
        IMPORTANTE: se houver uma tentativa de atualizar com um token revogado, revogar todos os tokens desse usuario!!!
        */


        return Response.ok().build();
    }

    @POST
    @Path("logout")
    public Response logout() {
        return Response.ok().build();
    }

    @GET
    @Path("me")
    public Response me() {
        return Response.ok().build();
    }

}
