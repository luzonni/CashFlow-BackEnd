package com.luzonni.cashflow.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginRequest {

    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String password;
    @NotNull
    private UUID deviceId;

}
