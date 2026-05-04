package com.luzonni.cashflow.shared.rest.exception;

import com.luzonni.cashflow.shared.dto.ErrorResponse;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper
        implements ExceptionMapper<ConflictException> {

    @Override
    public Response toResponse(
            ConflictException exception
    ) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(
                        Response.Status.CONFLICT,
                        exception.getMessage()
                ))
                .build();
    }
}
