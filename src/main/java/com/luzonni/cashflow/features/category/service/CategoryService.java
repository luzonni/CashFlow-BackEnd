package com.luzonni.cashflow.features.category.service;

import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.group_category.repository.GroupCategoryRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.exception.domain.AppException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryService {

    private final CategoryRepository repository;
    private final UserRepository userRepository;
    private final GroupCategoryRepository groupRepository;

    public CategoryService(
            CategoryRepository repository,
            UserRepository userRepository,
            GroupCategoryRepository groupRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public List<CategoryResponse> listAll(UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return List.of();
        }
        User user = userOptional.get();
        List<Category> userCategories = repository
                .listAllPerUser(user)
                .stream()
                .toList();
        return userCategories
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional
    public CategoryResponse create(
            UUID userId,
            CategoryRequest request
    ) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new AppException(Response.Status.NOT_FOUND, ErrorCode.ENTITY_NOT_FOUND, "User not found");
        }
        User user = optionalUser.get();
        Category category = repository.find(
                "name = ?1 and user = ?2 and deleted = true",
                request.getName(),
                user
        ).firstResult();
        if (category != null) {
            category.setDeleted(false);
            category.setColor(request.getColor());
            repository.persist(category);
            return new CategoryResponse(category);
        }
        category = new Category();
        category.setUser(user);
        category.setColor(request.getColor());
        category.setName(request.getName());
        GroupCategory group = groupRepository.findById(request.getGroupId());
        category.setGroup(group);
        try {
            repository.persist(category);
        } catch (Exception e) {
            throw new AppException(Response.Status.CONFLICT, ErrorCode.ENTITY_ALREADY_EXISTS, "This category already exists");
        }
        return new CategoryResponse(category);
    }

    @Transactional
    public CategoryResponse update(
            UUID userId,
            Long categoryId,
            CategoryRequest request
    ) {
        Category category = repository.find(
                "id = ?1 and user.id = ?2 and deleted = false",
                categoryId, userId
        ).firstResult();
        category.setName(request.getName());
        category.setColor(request.getColor());
        repository.persist(category);
        return new CategoryResponse(category);
    }

    @Transactional
    public void delete(UUID userId, Long categoryId) {
        Category category = repository.find(
                "id = ?1 and user.id = ?2 and deleted = false",
                categoryId,
                userId
        ).firstResult();
        if (category == null) {
            throw new NotFoundException("Category not found");
        }
        category.setDeleted(true);
        repository.persist(category);
    }

}
