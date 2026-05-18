package com.luzonni.cashflow.features.exception.domain;

import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final Response.Status status;
    private final ErrorCode code;

    public AppException(Response.Status status, ErrorCode code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

}
