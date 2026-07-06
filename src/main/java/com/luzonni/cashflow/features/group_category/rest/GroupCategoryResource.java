package com.luzonni.cashflow.features.group_category.rest;

import com.luzonni.cashflow.features.group_category.dto.GroupCategoryResponse;
import com.luzonni.cashflow.features.group_category.service.GroupCategoryService;
import com.luzonni.cashflow.features.group_category.dto.GroupCategoryRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

@RolesAllowed("USER")
@Path("/category_group")
public class GroupCategoryResource {

    private final GroupCategoryService service;
    private final JsonWebToken token;

    @Inject
    public GroupCategoryResource(
            GroupCategoryService service,
            JsonWebToken token
    ) {
        this.service = service;
        this.token = token;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(
            @Valid GroupCategoryRequest request
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        GroupCategoryResponse newCategory = service.create(request, userId);
        return Response
                .status(Response.Status.CREATED)
                .entity(newCategory)
                .build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(
            @PathParam("id") Long id,
            @Valid GroupCategoryRequest request
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        GroupCategoryResponse updatedCategory = service.update(userId, id, request);
        return Response
                .ok(updatedCategory)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCategories() {
        UUID userId = UUID.fromString(token.getSubject());
        return Response
                .ok(service.findAll(userId))
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCategory(
            @PathParam("id")
            Long id
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        service.delete(userId, id);
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
