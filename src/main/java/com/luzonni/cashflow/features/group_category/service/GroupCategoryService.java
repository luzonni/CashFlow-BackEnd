package com.luzonni.cashflow.features.group_category.service;

import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.group_category.dto.GroupCategoryRequest;
import com.luzonni.cashflow.features.group_category.dto.GroupCategoryResponse;
import com.luzonni.cashflow.features.group_category.repository.GroupCategoryRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.shared.dto.ErrorCode;
import com.luzonni.cashflow.shared.exceptions.BusinessException;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class GroupCategoryService {

    private final GroupCategoryRepository repository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public GroupCategoryService(
            GroupCategoryRepository repository,
            CategoryRepository categoryRepository,
            UserRepository userRepository
    ) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public List<GroupCategoryResponse> findAll() {
        return repository
                .find("deleted = false order by createdAt desc")
                .stream()
                .map((group) -> {
                    GroupCategoryResponse response = new GroupCategoryResponse(group);
                    List<CategoryResponse> categories = categoryRepository
                            .find("group.id = ?1 and deleted = false order by createdAt asc", group.getId())
                            .stream()
                            .map(CategoryResponse::new)
                            .toList();
                    if (!categories.isEmpty()) {
                        response.setCategories(categories);
                    }
                    return response;
                }).toList();
    }

    @Transactional
    public GroupCategoryResponse create(
            GroupCategoryRequest request,
            UUID userId
    ) throws ConflictException {
        User user = userRepository.getUserById(userId);
        GroupCategory group = repository.find(
                "name = ?1 and user = ?2 and deleted = true",
                request.getName(), user
        ).firstResult();
        if (group != null) {
            group.setDeleted(false);
            group.setDescription(request.getDescription());
            repository.persist(group);
            return new GroupCategoryResponse(group);
        }
        group = new GroupCategory();
        group.setUser(user);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        try {
            repository.persist(group);
        } catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
        return new GroupCategoryResponse(group);
    }

    @Transactional
    public void delete(UUID userId, Long id) {
        GroupCategory group = repository.find(
                "id = ?1 and user.id = ?2 and deleted = false",
                id, userId
        ).firstResult();
        if(group == null) {
            throw new NotFoundException("Category not found");
        }
        List<Category> byGroupId = categoryRepository.findByGroupId(id);
        for (Category category : byGroupId) {
            category.setDeleted(true);
            categoryRepository.persist(category);
        }
        group.setDeleted(true);
        repository.persist(group);
    }

    @Transactional
    public GroupCategoryResponse update(
            UUID userId,
            Long id,
            GroupCategoryRequest request
    ) throws NotFoundException, ConflictException {
        if(repository.count("user.id = ?1 and name = ?2 and deleted = true") > 1) {
            throw new BusinessException(
                    ErrorCode.NAME_RESERVED_BY_DELETED_ENTITY,
                    "This name is reserved by deleted entity"
            );
        }
        GroupCategory group = repository.find(
                "id = ?1 and user.id = ?2 and deleted = false",
                id, userId
        ).firstResult();
        if(group == null)
            throw new NotFoundException();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        repository.persist(group);
        GroupCategoryResponse response = new GroupCategoryResponse(group);
        response.setCategories(categoryRepository
                .findByGroupId(id)
                .stream()
                .map(CategoryResponse::new)
                .toList()
        );
        return response;
    }

}
