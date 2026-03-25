package io.luzonni.mindbudget.rest.resources.user;

import io.luzonni.mindbudget.domain.model.user.User;
import io.luzonni.mindbudget.repository.user.UserRepository;
import io.luzonni.mindbudget.rest.dto.user.UserRequest;
import io.luzonni.mindbudget.rest.dto.error.ResponseError;
import io.luzonni.mindbudget.rest.dto.user.UserResponse;
import io.luzonni.mindbudget.util.PasswordUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Path("/user")
public class UserResource {

    private final UserRepository userRepository;
    private final Validator validator;
    private final JsonWebToken jwt;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator, JsonWebToken jwt) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.jwt = jwt;
    }

    @GET
    @RolesAllowed("user")
    public Response getUser() {
        UUID userId = UUID.fromString(jwt.getSubject());
        Optional<User> option = userRepository.findById(userId);
        if(option.isPresent()) {
            return Response
                    .ok(UserResponse.from(option.get()))
                    .build();
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(
            UserRequest userRequest
    ) {
        Set<ConstraintViolation<UserRequest>> validate = validator.validate(userRequest);
        if (!validate.isEmpty()) {
            return ResponseError.createError(validate).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Email already registered")
                    .build();
        }
        if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Username already registered")
                    .build();
        }
        User user = new User();
        user.setUsername(userRequest.getUsername().toLowerCase().trim());
        user.setEmail(userRequest.getEmail());
        user.setBirthday(userRequest.getBirthday());
        String hashedPassword = PasswordUtil.hash(userRequest.getPassword());
        user.setPasswordHash(hashedPassword);
        userRepository.persist(user);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteUser(
            @PathParam("id")
            UUID userId
    ) {
        Optional<User> option = userRepository.findById(userId);
        if(option.isPresent()) {
            User user = option.get();
            userRepository.delete(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("id")
            UUID userId,
            UserRequest userRequest
    ) {

        Optional<User> option = userRepository.findById(userId);
        if(option.isPresent()) {
            User user = option.get();
            if(userRequest.getUsername() != null)
                user.setUsername(userRequest.getUsername().toLowerCase().trim());
            if(userRequest.getEmail() != null)
                user.setEmail(userRequest.getEmail());
            if(userRequest.getBirthday() != null)
                user.setBirthday(userRequest.getBirthday());
            if(userRequest.getPassword() != null) {
                String hashedPassword = PasswordUtil.hash(userRequest.getPassword());
                user.setPasswordHash(hashedPassword);
            }
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_MODIFIED).build();
    }




}
