package com.luzonni.cashflow.features.exchange.rest;

import com.luzonni.cashflow.features.exchange.service.ExchangeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("exchange")
@RolesAllowed("USER")
public class ExchangeResource {

    private final ExchangeService exchangeService;

    public ExchangeResource(
            ExchangeService exchangeService
    ) {
        this.exchangeService = exchangeService;
    }

    @GET
    @Path("currency")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCurrency() {
        return Response
                .ok(exchangeService.getCurrency())
                .build();
    }

}
