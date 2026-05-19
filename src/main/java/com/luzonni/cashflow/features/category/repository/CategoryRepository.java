package com.luzonni.cashflow.features.category.repository;

import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.category.domain.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {

    @Override
    public Category findById(Long id) {
        Optional<Category> optional = find(
                "id = ?1 and deleted = false",
                id
        ).firstResultOptional();
        if(optional.isEmpty()) {
            throw new AppException(
                    Response.Status.NOT_FOUND,
                    ErrorCode.ENTITY_NOT_FOUND,
                    "Category not found or is deprecated"
            );
        }
        return optional.get();
    }

    public List<Category> listAllPerUser(User user) {
        return find("userId = ?1 and deleted = false order by createdAt", user.getId()).list();
    }
    
    public List<Category> findByGroupId(Long id) {
        return find("group.id", id).list();
    }

}
