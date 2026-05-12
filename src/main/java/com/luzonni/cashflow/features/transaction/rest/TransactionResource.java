package com.luzonni.cashflow.features.transaction.rest;

import com.luzonni.cashflow.features.transaction.dto.TransactionResponse;
import com.luzonni.cashflow.features.transaction.service.TransactionService;
import com.luzonni.cashflow.features.transaction.dto.TransactionRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Path("/transaction")
@RolesAllowed("USER")
public class TransactionResource {

    private final TransactionService service;
    private final JsonWebToken jwt;

    @Inject
    public TransactionResource(
            TransactionService service,
            JsonWebToken jwt
    ) {
        this.service = service;
        this.jwt = jwt;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactions() {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<TransactionResponse> list = service.listAll(userId);
        return Response.ok(list).build();
    }

    @GET
    @Path("between")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactionsWithData(
            @QueryParam("start") LocalDate start,
            @QueryParam("end") LocalDate end
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<TransactionResponse> list = service.listWithDate(userId, start, end);
        return Response.ok(list).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(
            @Valid TransactionRequest request
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        TransactionResponse response = service.create(userId, request);
        return Response
                .status(Response.Status.CREATED)
                .entity(response)
                .build();
    }


}
