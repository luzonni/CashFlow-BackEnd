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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserCategoryService {


    private final CategoryRepository repository;
    private final UserRepository userRepository;
    private final GroupCategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public UserCategoryService(
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
        List<Category> userCategories = repository.listAllPerUser(user);
        return userCategories.stream().map(CategoryResponse::new).toList();
    }

    @Transactional
    public CategoryResponse create(
            UUID userId,
            CategoryRequest request
    ) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        Category userCategory = new Category();
        userCategory.setUser(user);
        userCategory.setName(request.getName());
        GroupCategory category = categoryRepository.findById(request.getBaseCategoryId());
        userCategory.setGroup(category);
        repository.persist(userCategory);
        return new CategoryResponse(userCategory);
    }

    @Transactional
    public void delete(UUID userId, UUID categoryId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return;
        }
        User user = optionalUser.get();
        Optional<Category> optional = repository.findByUUID(categoryId);
        if (optional.isEmpty()) {
            return;
        }
        Category userCategory = optional.get();
        if (userCategory.getUser().getId().equals(user.getId())) {
            boolean isUsed = transactionRepository.existsByUserCategoryId(categoryId);
            if (isUsed) {
                userCategory.setDeleted(true);
            } else {
                repository.delete(userCategory);
            }
        }
    }

}
