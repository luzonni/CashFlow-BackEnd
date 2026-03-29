package com.luzonni.cashflow.features.transaction.rest;

import com.luzonni.cashflow.features.usercategory.domain.UserCategory;
import com.luzonni.cashflow.features.transaction.domain.Transaction;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.usercategory.repository.UserCategoryRepository;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.features.transaction.dto.TransactionRequest;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;
import java.util.UUID;

@Path("/transaction")
public class TransactionResource {

    private final TransactionRepository repository;
    private final UserRepository userRepository;
    private final UserCategoryRepository  userCategoryRepository;
    private final JsonWebToken jwt;

    @Inject
    public TransactionResource(
            TransactionRepository  transactionRepository,
            UserCategoryRepository  userCategoryRepository,
            UserRepository userRepository,
            Validator validator,
            JsonWebToken jwt

    ) {
        this.userCategoryRepository = userCategoryRepository;
        this.repository = transactionRepository;
        this.userRepository = userRepository;
        this.jwt = jwt;
    }

    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listTransactions() {

        return Response.ok(repository.findAll()).build();
    }

    @POST
    @Transactional
    @RolesAllowed("user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTransaction(
            @Valid TransactionRequest transactionRequest
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID userCategoryId = UUID.fromString(transactionRequest.getUserCategoryId());
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<UserCategory> userCategoryOpt = userCategoryRepository.findByUUID(userCategoryId);
        if (userOpt.isEmpty() || userCategoryOpt.isEmpty()) {
            return  Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not found or category not found")
                    .build();
        }
        Transaction transactionEntity = getTransaction(
                transactionRequest,
                userOpt.get(),
                userCategoryOpt.get()
        );
        repository.persist(transactionEntity);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @RolesAllowed("user")
    public Response deleteTransaction(
            @PathParam("id")
            UUID transactionId
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Transaction> transactionOpt = repository.findById(transactionId);
        if (userOpt.isEmpty() || transactionOpt.isEmpty()) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("User not be found")
                    .build();
        }
        User user = userOpt.get();
        Transaction transaction = transactionOpt.get();
        if(!user.getId().equals(transaction.getUser().getId())) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("This transaction does not belong to this user.")
                    .build();
        }
        repository.delete(transaction);
        return Response.noContent().build();
    }

    private static Transaction getTransaction(
            TransactionRequest transactionRequest,
            User userEntity,
            UserCategory userCategoryEntity
    ) {
        Transaction transactionEntity = new Transaction();
        transactionEntity.setUser(userEntity);
        transactionEntity.setUserCategory(userCategoryEntity);
        transactionEntity.setAmount(transactionRequest.getAmount());
        transactionEntity.setDescription(transactionRequest.getDescription());
        transactionEntity.setTransactionDate(transactionRequest.getTransactionDate());
        return transactionEntity;
    }

}
