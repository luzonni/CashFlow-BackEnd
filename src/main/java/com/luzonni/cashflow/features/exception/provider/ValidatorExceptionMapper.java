package com.luzonni.cashflow.features.exception.provider;

import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.exception.dto.ErrorResponse;
import com.luzonni.cashflow.features.exception.dto.FieldError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class ValidatorExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        List<FieldError> errors = exception.getConstraintViolations()
                .stream()
                .map(this::toFieldError)
                .toList();
        ErrorResponse response = new ErrorResponse(
                ErrorCode.VALIDATION_ERROR.name(),
                "Validation failed",
                errors
        );
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }

    private FieldError toFieldError(ConstraintViolation<?> violation) {
        String[] field = violation.getPropertyPath().toString().split("\\.");
        return new FieldError(
                field[field.length - 1],
                violation.getMessage()
        );
    }
}
