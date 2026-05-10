package com.luzonni.cashflow.shared.dto;

import jakarta.ws.rs.core.Response;
import lombok.Data;

@Data
public class ErrorResponse {

    private Response.Status status;
    private ErrorCode errorCode;
    private String message;

    public ErrorResponse(Response.Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponse(ErrorCode code, String message) {
        this.errorCode = code;
        this.message = message;
    }

}
