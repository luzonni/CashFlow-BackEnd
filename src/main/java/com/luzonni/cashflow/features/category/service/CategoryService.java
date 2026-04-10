package com.luzonni.cashflow.features.category.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.shared.enums.TransactionType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setType(TransactionType.valueOf(request.getType().toUpperCase()));
        if(request.getParentId() != null) {
            Category parent = repository.findById(request.getParentId());
            if(parent == null) {
                return null; //parent not found
            }
            if(parent.getParent() != null) {
                return null; //O parente não pode ser filha de outra categoria
            }
            category.setParent(parent);
        }
        repository.persist(category);
        return new CategoryResponse(category);
    }

    public List<CategoryResponse> findAll() {
        return repository.listAll().stream().map(CategoryResponse::new).toList();
    }

    @Transactional
    public List<Long> delete(Long id) {
        return repository.deleteCascade(id);
    }

}
