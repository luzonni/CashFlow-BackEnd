package com.luzonni.cashflow.features.category.repository;

import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.category.domain.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {


    public boolean existsByName(String name) {
        return count("name = ?1", name) >= 1;
    }

    public Optional<Category> findByUUID(UUID id) {
        return find("id", id).firstResultOptional();
    }

    public List<Category> listAllPerUser(User user) {
        return find("userId = ?1", user.getId()).list();
    }

    public List<Category> findByGroupId(Long id) {
        return find("group.id", id).list();
    }
}
