package io.luzonni.mindbudget.rest.dto.error;

import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
public class ResponseError {

    public static final int UNPROCESSABLE_ENTITY_STATUS = 422;

    private String message;
    private Collection<FieldError> errors;

    public ResponseError(String message, Collection<FieldError> errors) {
        this.message = message;
        this.errors = errors;
    }

    public static <T> ResponseError createError(Set<ConstraintViolation<T>> violations) {
        List<FieldError> errors = violations.stream().map((cv) ->
                        new FieldError(cv.getPropertyPath().toString(), cv.getMessage()))
                .toList();
        String message = "Validation error";
        return new ResponseError(message, errors);
    }

    public Response withStatusCode(int statusCode) {
        return Response
                .status(statusCode)
                .entity(this)
                .build();
    }
}