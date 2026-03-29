package com.luzonni.cashflow.features.category.rest;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.shared.enums.TransactionType;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/category")
public class CategoryResource {

    private final CategoryRepository repository;
    private final Validator validator;

    @Inject
    public CategoryResource(CategoryRepository repository, Validator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCategory(
            @Valid
            CategoryRequest request
    ) {
        if(repository.existsWithName(request.getName())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("There is already a category with that name")
                    .build();
        }
        Category category = new Category();
        category.setName(request.getName());
        category.setType(TransactionType.valueOf(request.getType().toUpperCase()));
        if(request.getParentId() != null) {
            Category parent = repository.findById(request.getParentId());
            if(parent == null) {
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity("Parent category not found")
                        .build();
            }
            category.setParent(parent);
        }
        repository.persist(category);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteCategory(
            @PathParam("id")
            Long id
    ) {
        Category category = repository.findById(id);
        if(category != null) {
        repository.delete(category);
            return Response
                    .status(Response.Status.NO_CONTENT)
                    .build();
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

}
