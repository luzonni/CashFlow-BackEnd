package com.luzonni.cashflow.features.exception.dto;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<FieldError> errors
) {}
