package com.luzonni.cashflow.features.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotEmpty
    private String refreshToken;

}
