package com.luzonni.cashflow.features.group_category.rest;

import com.luzonni.cashflow.features.group_category.dto.GroupCategoryResponse;
import com.luzonni.cashflow.features.group_category.service.GroupCategoryService;
import com.luzonni.cashflow.features.group_category.dto.GroupCategoryRequest;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.UUID;

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
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(
            @Valid GroupCategoryRequest request
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        /*
            TODO
            existe um problema aqui, caso a categoria tenha sido deletada e o usuario crie uma nova com o mesmo nome,
            a categoria deve ser ativa novamente! Não criar outra!
         */
        try {
            GroupCategoryResponse newCategory = service.create(request, userId);
            if (newCategory == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .build();
            }
            return Response
                    .status(Response.Status.CREATED)
                    .entity(newCategory)
                    .build();
        }catch (ConflictException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @RolesAllowed("USER")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(
            @PathParam("id") Long id,
            @Valid GroupCategoryRequest request
    ) {
        try {
            GroupCategoryResponse updatedCategory = service.update(id, request);
            if (updatedCategory == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .build();
            }
            return Response
                    .ok(updatedCategory)
                    .build();
        }catch (ConflictException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .build();
        }
    }

    @GET
    @RolesAllowed("USER")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCategories() {
        return Response
                .ok(service.findAll())
                .build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("USER")
    public Response deleteCategory(
            @PathParam("id")
            Long id
    ) {
        service.delete(id);
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
