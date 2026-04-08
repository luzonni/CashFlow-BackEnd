package com.luzonni.cashflow.shared.dto;

import jakarta.ws.rs.core.Response;
import lombok.Data;

@Data
public class ErrorResponse {

    private Response.Status status;
    private String message;

    public ErrorResponse(Response.Status status, String message) {
        this.status = status;
        this.message = message;
    }

}
