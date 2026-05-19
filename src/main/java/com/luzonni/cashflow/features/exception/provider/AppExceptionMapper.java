package com.luzonni.cashflow.features.exception.provider;

import com.luzonni.cashflow.features.exception.dto.ErrorResponse;
import com.luzonni.cashflow.features.exception.domain.AppException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Provider
public class AppExceptionMapper implements ExceptionMapper<AppException> {

    @Override
    public Response toResponse(
            AppException exception
    ) {
        return Response
                .status(exception.getStatus())
                .entity(new ErrorResponse(
                        exception.getCode().name(),
                        exception.getMessage(),
                        List.of()
                ))
                .build();
    }

}
