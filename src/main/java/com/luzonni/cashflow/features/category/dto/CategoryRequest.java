package com.luzonni.cashflow.features.category.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Color is required")
    private String color;
    @NotNull
    private Long groupId;

}
