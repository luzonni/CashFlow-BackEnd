package com.luzonni.cashflow.shared.rest.exception;

import com.luzonni.cashflow.shared.dto.ErrorResponse;
import com.luzonni.cashflow.shared.exceptions.BusinessException;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(
            BusinessException exception
    ) {
        return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse(
                        exception.getErrorCode(),
                        exception.getMessage()
                ))
                .build();
    }
}
