package com.luzonni.cashflow.features.installment.rest;

import com.luzonni.cashflow.features.installment.dto.InstallmentRequest;
import com.luzonni.cashflow.features.installment.dto.InstallmentResponse;
import com.luzonni.cashflow.features.installment.service.InstallmentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

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

}
