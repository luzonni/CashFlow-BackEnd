package com.luzonni.cashflow.features.category.rest;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.service.CategoryService;
import com.luzonni.cashflow.shared.enums.TransactionType;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/category")
public class CategoryResource {

    private final CategoryService service;

    @Inject
    public CategoryResource(CategoryService service) {
        this.service = service;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCategory(
            @Valid CategoryRequest request
    ) {
        CategoryResponse newCategory = service.create(request);
        if (newCategory == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .build();
        }
        return Response
                .status(Response.Status.CREATED)
                .entity(newCategory)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listCategories() {
        return Response
                .ok(service.findAll())
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteCategory(
            @PathParam("id")
            Long id
    ) {
        return Response
                .status(Response.Status.OK)
                .entity(service.delete(id))
                .build();
    }

}
