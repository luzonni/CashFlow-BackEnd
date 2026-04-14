package com.luzonni.cashflow.features.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotEmpty(message = "the name cannot be empty")
    private String name;
    @NotNull
    private String color;
    @NotNull
    private Long groupId;

}
