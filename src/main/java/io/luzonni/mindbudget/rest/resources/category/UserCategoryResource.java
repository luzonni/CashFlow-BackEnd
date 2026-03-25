package io.luzonni.mindbudget.rest.resources.category;

import io.luzonni.mindbudget.domain.model.category.Category;
import io.luzonni.mindbudget.domain.model.category.UserCategory;
import io.luzonni.mindbudget.domain.model.user.User;
import io.luzonni.mindbudget.enums.TransactionType;
import io.luzonni.mindbudget.repository.category.CategoryRepository;
import io.luzonni.mindbudget.repository.category.UserCategoryRepository;
import io.luzonni.mindbudget.repository.user.UserRepository;
import io.luzonni.mindbudget.rest.dto.category.UserCategoryRequest;
import io.luzonni.mindbudget.rest.dto.error.ResponseError;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Path("/user/category")
public class UserCategoryResource {

    private final UserCategoryRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final Validator validator;
    private final JsonWebToken token;

    @Inject
    public UserCategoryResource(
            UserCategoryRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            Validator validator,
            JsonWebToken token
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.validator = validator;
        this.token = token;
    }

    @POST
    @Transactional
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCategory(
            UserCategoryRequest userCategoryRequest
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        Set<ConstraintViolation<UserCategoryRequest>> validate = validator.validate(userCategoryRequest);
        if (!validate.isEmpty()) {
            return ResponseError.createError(validate).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }
        if(repository.existsByName(userCategoryRequest.getName())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("There is already a category with that name.")
                    .build();
        }
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        User user = optionalUser.get();
        UserCategory userCategory = new UserCategory();
        userCategory.setUser(user);
        userCategory.setName(userCategoryRequest.getName());
        userCategory.setType(TransactionType.valueOf(userCategoryRequest.getType().toUpperCase()));
        if(userCategoryRequest.getBaseCategoryId() != null) {
            Category category = categoryRepository.findById(userCategoryRequest.getBaseCategoryId());
            if(category == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .build();
            }
            userCategory.setBaseCategory(category);
        }
        repository.persist(userCategory);
        return Response
                .status(Response.Status.CREATED)
                .build();
    }

    @DELETE
    @Transactional
    @Path("{categoryId}")
    @RolesAllowed("user")
    public Response deleteCategory(
            @PathParam("categoryId")
            UUID categoryId
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            return  Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }
        Optional<UserCategory> optional = repository.findByUUID(categoryId);
        if(optional.isEmpty()) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Category not found")
                    .build();
        }
        UserCategory userCategory = optional.get();
        if(!userCategory.getUser().getId().equals(optionalUser.get().getId())) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("This category does not belong to this user, so they do not have permission to delete it.")
                    .build();
        }
        repository.delete(userCategory);
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
