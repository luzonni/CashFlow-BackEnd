package com.luzonni.cashflow.features.usercategory.rest;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.usercategory.domain.UserCategory;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.shared.enums.TransactionType;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.usercategory.repository.UserCategoryRepository;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.features.usercategory.dto.UserCategoryRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.UUID;

//TODO mudar essa rota para ficar padrão REST
@Path("/user/category")
public class UserCategoryResource {

    private final UserCategoryRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final JsonWebToken token;

    @Inject
    public UserCategoryResource(
            UserCategoryRepository repository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            JsonWebToken token
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.token = token;
    }

    @POST
    @Transactional
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postCategory(
            @Valid
            UserCategoryRequest request
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        if(repository.existsByName(request.getName())) {
            return Response //TODO e se outro usuario criar uma categoria com o mesmo nome?
                    .status(Response.Status.CONFLICT)
                    .entity("There is already a category with that name.")
                    .build();
        }
        Optional<User> optionalUser = userRepository.findById(userId); //TODO não precisa validar o usuario, o token ja faz isso, esta sendo redundante desnecessariamente.
        if(optionalUser.isEmpty()) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
        User user = optionalUser.get();
        UserCategory userCategory = new UserCategory();
        userCategory.setUser(user);
        userCategory.setName(request.getName());
        userCategory.setType(TransactionType.valueOf(request.getType().toUpperCase())); //TODO qualquer conflito da erro 500... errado!
        if(request.getBaseCategoryId() != null) {
            Category category = categoryRepository.findById(request.getBaseCategoryId());
            if(category == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .build();
            }
            userCategory.setBaseCategory(category);
        }
        repository.persist(userCategory);
        return Response // TODO falta de retorno util...
                .status(Response.Status.CREATED)
                .build();
    }

    @DELETE
    @Transactional
    @Path("{categoryId}")
    @RolesAllowed("user")
    public Response deleteCategory( //TODO função rest sem idempotência
            @PathParam("categoryId")
            UUID categoryId
    ) {
        UUID userId = UUID.fromString(token.getSubject());
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            return  Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not found") //TODO padronizar retorno...
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
        if(!userCategory.getUser().getId().equals(optionalUser.get().getId())) { //TODO isso é uma regra de negocio, não pode ficar aqui!
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("This category does not belong to this user, so they do not have permission to delete it.")
                    .build();
        }
        repository.delete(userCategory);
        return Response
                .status(Response.Status.NO_CONTENT)
                .build();
    }

}
