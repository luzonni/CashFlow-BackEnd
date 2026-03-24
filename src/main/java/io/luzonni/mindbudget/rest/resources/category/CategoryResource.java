package io.luzonni.mindbudget.rest.resources.category;

import io.luzonni.mindbudget.domain.model.category.Category;
import io.luzonni.mindbudget.enums.TransactionType;
import io.luzonni.mindbudget.repository.category.CategoryRepository;
import io.luzonni.mindbudget.rest.dto.category.CategoryRequest;
import io.luzonni.mindbudget.rest.dto.error.ResponseError;
import io.luzonni.mindbudget.rest.dto.user.UserRequest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Set;

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
            CategoryRequest categoryRequest
    ) {
        Set<ConstraintViolation<CategoryRequest>> validate = validator.validate(categoryRequest);
        if (!validate.isEmpty()) {
            return ResponseError.createError(validate).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setType(TransactionType.valueOf(categoryRequest.getType().toUpperCase()));
        if(categoryRequest.getParentId() != null) {
            Category parent = repository.findById(categoryRequest.getParentId());
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

}
