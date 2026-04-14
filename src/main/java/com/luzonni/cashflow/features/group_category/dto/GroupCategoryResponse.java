package com.luzonni.cashflow.features.group_category.dto;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class GroupCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private LocalDateTime createAt;
    private List<CategoryResponse> categories = new ArrayList<>();

    public GroupCategoryResponse(GroupCategory groupCategory) {
        this.id = groupCategory.getId();
        this.name = groupCategory.getName();
        this.description = groupCategory.getDescription();
        this.active = !groupCategory.getDeleted();
        this.createAt = groupCategory.getCreatedAt();
    }

}
