package io.luzonni.mindbudget.repository.category;

import io.luzonni.mindbudget.domain.model.category.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {


    public boolean existsWithName(
            @NotNull
            String name
    ) {
        return count("name = ?1", name) >= 1;
    }
}
