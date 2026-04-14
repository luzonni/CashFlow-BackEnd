package com.luzonni.cashflow.features.category.dto;

import com.luzonni.cashflow.features.category.type.TransactionType;
import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.category.domain.Category;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponse {

    private Long id;
    private String color;
    private String name;
    private TransactionType type;
    private Boolean active;
    private LocalDateTime createdAt;

    public CategoryResponse(Category userCategory) {
        this.id = userCategory.getId();
        this.color = userCategory.getColor();
        this.name = userCategory.getName();
        this.type = userCategory.getType();
        this.active = !userCategory.getDeleted();
        this.createdAt = userCategory.getCreatedAt();
    }

}
