package com.luzonni.cashflow.features.group_category.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupCategoryRequest {

    @NotEmpty(message = "Name is required")
    private String name;

    @Nullable
    private String description;

}
