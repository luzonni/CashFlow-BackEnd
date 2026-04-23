package com.luzonni.cashflow.features.category.dto;

import com.luzonni.cashflow.features.category.type.TransactionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotEmpty(message = "the name cannot be empty")
    private String name;
    @NotEmpty
    private String color;
    @NotNull
    private Long groupId;
    @NotNull
    private TransactionType type;

}
