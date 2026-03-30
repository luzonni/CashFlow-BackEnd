package com.luzonni.cashflow.features.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotEmpty
    private String refreshToken;

}
