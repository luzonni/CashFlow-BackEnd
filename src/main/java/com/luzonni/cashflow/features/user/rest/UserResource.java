package com.luzonni.cashflow.features.user.rest;

import com.luzonni.cashflow.features.settings.dto.SettingsRequest;
import com.luzonni.cashflow.features.user.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@Path("user")
@RolesAllowed("USER")
public class UserResource {

    private final UserService service;
    private final JsonWebToken token;

    @Inject
    public UserResource(
            UserService service,
            JsonWebToken token
    ) {
        this.service = service;
        this.token = token;
    }

    @PATCH
    @Path("settings")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeSettings(
            SettingsRequest request
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        service.changeSettings(userId, request);
        return Response.noContent().build();
    }

}
