package com.luzonni.cashflow.features.hi.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("hi")
public class HiResource {

    @GET
    public Response hi() {
        return Response.ok("Hi!").build();
    }

}
