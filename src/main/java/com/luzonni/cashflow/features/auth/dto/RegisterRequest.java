package com.luzonni.cashflow.features.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 20,
            message = "Username must be between 3 and 20 characters long"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username may contain only letters, numbers, and underscores"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character"
    )
    private String password;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate birthday;

}
