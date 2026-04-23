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
import com.luzonni.cashflow.shared.exceptions.ConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
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
                .listAll()
                .stream()
                .filter(GroupCategory::active)
                .map((group) -> {
                    GroupCategoryResponse response = new GroupCategoryResponse(group);
                    List<CategoryResponse> categories = categoryRepository
                            .find("group.id", group.getId())
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
    public GroupCategoryResponse create(GroupCategoryRequest request, UUID userId) throws ConflictException {
        GroupCategory group = new GroupCategory();
        User user = userRepository.getUserById(userId);
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
    public void delete(Long id) {
        List<Category> byGroupId = categoryRepository.findByGroupId(id);
        for (Category category : byGroupId) {
            category.setDeleted(true);
            /*
                TODO
                Na verdade seria legal usar o metodo do service de categoria, onde, caso a categoria esteja sendo
                usada, ela é apenas marcada como deletada, e caso não tenha nada referenciando ela, ela é realmente
                apagada do banco.
                e caso todas as categorias do grupo tenham sido deletadas, seria interessante deletar o grupo tambem
                ao inves de tambem marca-lo como deletado!
             */
            categoryRepository.persist(category); // persistindo alteração do deleted!
        }
        GroupCategory group = repository.findById(id);
        group.setDeleted(true);
        repository.persist(group);
    }

    @Transactional
    public GroupCategoryResponse update(Long id, GroupCategoryRequest request) {
        GroupCategory group = repository.findById(id);
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
