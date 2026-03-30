package com.luzonni.cashflow.features.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 20, message = "Username deve ter entre 3 e 20 caracteres")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username só pode conter letras, números e underscore (_), sem espaços"
    )
    private String username;
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    @NotBlank(message = "Senha é obrigatória")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Senha deve ter no mínimo 8 caracteres, incluindo maiúscula, minúscula, número e caractere especial"
    )
    private String password;
    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate birthday;

}
