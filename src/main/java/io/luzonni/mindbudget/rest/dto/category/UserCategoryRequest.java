package io.luzonni.mindbudget.rest.dto.category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCategoryRequest {

    @NotEmpty(message = "the name cannot be empty")
    private String name;
    @NotNull
    private String type;
    @Nullable
    private Long baseCategoryId;


}
