package io.luzonni.mindbudget.rest.dto.category;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotNull
    private String name;

    @NotNull
    private String type;

    @Nullable
    private Long parentId;

}
