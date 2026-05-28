package com.luzonni.cashflow.features.recurrence.rest;

import com.luzonni.cashflow.features.recurrence.domain.Recurrence;
import com.luzonni.cashflow.features.recurrence.dto.RecurrenceRequest;
import com.luzonni.cashflow.features.recurrence.dto.RecurrenceResponse;
import com.luzonni.cashflow.features.recurrence.service.RecurrenceService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Path("recurrences")
@RolesAllowed("USER")
public class RecurrenceResource {

    private final RecurrenceService service;
    private final JsonWebToken jwt;

    @Inject
    public RecurrenceResource(
            RecurrenceService service,
            JsonWebToken jsw
    ) {
        this.service = service;
        this.jwt = jsw;
    }

    @GET
    public Response listAll() {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<RecurrenceResponse> response = service.listAll(userId);
        return Response
                .status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @POST
    public Response createRecurrence(@Valid RecurrenceRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        RecurrenceResponse response = service.create(userId, request);
        return  Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @PATCH
    @Path("{id}")
    public Response updateRecurrence(
            @PathParam("id") UUID id,
            RecurrenceRequest request
    ) {
        service.update(id, request);
        return Response.noContent().build();
    }

}
