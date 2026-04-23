package com.luzonni.cashflow.features.category.service;

import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import com.luzonni.cashflow.features.group_category.repository.GroupCategoryRepository;
import com.luzonni.cashflow.features.transaction.repository.TransactionRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import com.luzonni.cashflow.features.category.domain.Category;
import com.luzonni.cashflow.features.category.dto.CategoryRequest;
import com.luzonni.cashflow.features.category.dto.CategoryResponse;
import com.luzonni.cashflow.features.category.repository.CategoryRepository;
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryService {


    private final CategoryRepository repository;
    private final UserRepository userRepository;
    private final GroupCategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(
            CategoryRepository repository,
            UserRepository userRepository,
            GroupCategoryRepository categoryRepository,
            TransactionRepository transactionRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> listAll(UUID userId) throws ConflictException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return List.of();
        }
        User user = userOptional.get();
        List<Category> userCategories = repository
                .listAllPerUser(user)
                .stream()
                .filter(Category::active)
                .toList();
        return userCategories.stream().map(CategoryResponse::new).toList();
    }

    @Transactional
    public CategoryResponse create(
            UUID userId,
            CategoryRequest request
    ) throws ConflictException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        Category category = new Category();
        category.setUser(user);
        category.setColor(request.getColor());
        category.setName(request.getName());
        category.setType(request.getType());
        GroupCategory group = categoryRepository.findById(request.getGroupId());
        category.setGroup(group);
        try {
            repository.persist(category);
        }catch (Exception e) {
            throw new ConflictException(e.getMessage());
        }
        return new CategoryResponse(category);
    }

    @Transactional
    public void delete(UUID userId, UUID categoryId) {
        Optional<Category> optional = repository.findByUUID(categoryId);
        if (optional.isEmpty()) {
            return;
        }
        Category userCategory = optional.get();
        if (userCategory.getUser().getId().equals(userId)) {
            boolean isUsed = transactionRepository.existsByUserCategoryId(categoryId);
            if (isUsed) {
                userCategory.setDeleted(true);
            } else {
                repository.delete(userCategory);
            }
        }
    }

}
