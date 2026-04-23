package com.luzonni.cashflow.features.category.rest;

import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import com.luzonni.cashflow.features.category.service.CategoryService;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.UUID;

@Path("/category")
@RolesAllowed("USER")
public class CategoryResource {

    private final CategoryService service;
    private final JsonWebToken token;

    @Inject
    public CategoryResource(
            CategoryService service,
            JsonWebToken token
    ) {
        this.service = service;
        this.token = token;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @Valid
            CategoryRequest request
    ) {
        try {
            UUID userId = UUID.fromString(token.getSubject());
            CategoryResponse response = service.create(userId, request);
            if (response == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .build();
            }
            return Response
                    .status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (ConflictException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        UUID userId = UUID.fromString(token.getSubject());
        List<CategoryResponse> list = service.listAll(userId);
        return Response
                .ok(list)
                .build();
    }

    @DELETE
    @Path("{categoryId}")
    public Response delete(
            @PathParam("categoryId")
            UUID categoryId
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        service.delete(userId, categoryId);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
