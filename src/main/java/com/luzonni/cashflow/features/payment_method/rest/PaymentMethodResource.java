package com.luzonni.cashflow.features.payment_method.rest;

import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodRequest;
import com.luzonni.cashflow.features.payment_method.dto.PaymentMethodResponse;
import com.luzonni.cashflow.features.payment_method.service.PaymentMethodService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Path("/payment_method")
@RolesAllowed("USER")
public class PaymentMethodResource {

    private final PaymentMethodService service;
    private final JsonWebToken token;

    @Inject
    public PaymentMethodResource(
            PaymentMethodService paymentMethodService,
            JsonWebToken token
    ) {
        this.service = paymentMethodService;
        this.token = token;
    }

    @GET
    public Response listAll() {
        UUID userId = UUID.fromString(token.getSubject());
        List<PaymentMethodResponse> list = service.listAll(userId);
        return Response
                .ok(list)
                .build();
    }

    @POST
    public Response create(@Valid  PaymentMethodRequest pm) {
        UUID userId = UUID.fromString(token.getSubject());
        PaymentMethodResponse response = service.create(userId, pm);
        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @PUT
    @Path("{id}")
    public Response update(
            @PathParam("id") Long id,
            @Valid PaymentMethodRequest pm
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        PaymentMethodResponse response = service.update(userId, id, pm);
        return Response
                .ok(response)
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) {
        UUID userId = UUID.fromString(token.getSubject());
        service.delete(userId, id);
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
