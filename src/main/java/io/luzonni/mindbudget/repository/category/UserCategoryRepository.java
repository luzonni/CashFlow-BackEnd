package io.luzonni.mindbudget.repository.category;

import io.luzonni.mindbudget.domain.model.category.UserCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserCategoryRepository implements PanacheRepository<UserCategory> {


    public boolean existsByName(String name) {
        return count("name = ?1", name) >= 1;
    }

    public Optional<UserCategory> findByUUID(UUID id) {
        return find("id", id).firstResultOptional();
    }
}
