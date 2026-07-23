package com.luzonni.cashflow.features.installment.rest;

import com.luzonni.cashflow.features.installment.dto.InstallmentRequest;
import com.luzonni.cashflow.features.installment.dto.InstallmentResponse;
import com.luzonni.cashflow.features.installment.service.InstallmentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RolesAllowed("USER")
@Path("installment")
public class InstallmentResource {

    private final InstallmentService service;
    private final JsonWebToken  jwt;

    @Inject
    public InstallmentResource(
           InstallmentService service,
            JsonWebToken jwt
    ) {
        this.service = service;
        this.jwt = jwt;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid InstallmentRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        InstallmentResponse response = service.create(userId, request);
        return Response.ok(response).build();
    }

    @GET
    @Path("percent/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response percent(
            @PathParam("id") Long id
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Map<UUID, Boolean> percent = service.getPercent(userId, id);
        return Response.ok(
                percent
        ).build();
    }

    @GET
    public Response list() {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<InstallmentResponse> response = service.list(userId);
        return Response.ok(response).build();
    }

}
