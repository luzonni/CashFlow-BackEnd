package com.luzonni.cashflow.features.category.dto;

import com.luzonni.cashflow.features.category.domain.Category;
import lombok.Data;

@Data
public class CategoryResponse {

    private Long id;
    private String name;
    private String type;
    private CategoryResponse parent;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.type = category.getType().name();
        if(category.getParent() != null) {
            this.parent = new CategoryResponse(category.getParent());
        }
    }

}
