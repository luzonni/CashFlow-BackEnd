package com.luzonni.cashflow.features.user.rest;

import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.dto.UserResponse;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.UUID;

@Path("/user")
public class UserResource {

    private final UserRepository userRepository;
    private final JsonWebToken jwt;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator, JsonWebToken jwt) {
        this.userRepository = userRepository;
        this.jwt = jwt;
    }

    @DELETE
    @Transactional
    @RolesAllowed("user")
    public Response deleteUser() {
        UUID userId = UUID.fromString(jwt.getSubject());
        Optional<User> option = userRepository.findById(userId);
        if(option.isPresent()) {
            User user = option.get();
            userRepository.delete(user);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

//    @PUT
//    @Transactional
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response updateUser(
//            UserRequest userRequest
//    ) {
//        UUID userId = UUID.fromString(jwt.getSubject());
//        Optional<User> option = userRepository.findById(userId);
//        if(option.isPresent()) {
//            User user = option.get();
//            if(userRequest.getUsername() != null)
//                user.setUsername(userRequest.getUsername().toLowerCase().trim());
//            if(userRequest.getEmail() != null)
//                user.setEmail(userRequest.getEmail());
//            if(userRequest.getBirthday() != null)
//                user.setBirthday(userRequest.getBirthday());
//            if(userRequest.getPassword() != null) {
//                String hashedPassword = PasswordUtil.hash(userRequest.getPassword());
//                user.setPasswordHash(hashedPassword);
//            }
//            return Response.noContent().build();
//        }
//        return Response.status(Response.Status.NOT_MODIFIED).build();
//    }




}
