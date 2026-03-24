package io.luzonni.mindbudget.repository.category;

import io.luzonni.mindbudget.domain.model.category.Category;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CategoryRepository implements PanacheRepository<Category> {



}
