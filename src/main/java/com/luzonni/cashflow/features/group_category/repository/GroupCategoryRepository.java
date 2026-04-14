package com.luzonni.cashflow.features.group_category.repository;

import com.luzonni.cashflow.features.group_category.domain.GroupCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GroupCategoryRepository implements PanacheRepository<GroupCategory> {


    public boolean existsWithName(
            @NotNull
            String name
    ) {
        return count("name = ?1", name) >= 1;
    }

    public List<Long> deleteCascade(Long id) {
        List<Long> list = new ArrayList<>();
        GroupCategory category = findById(id);
        if (category != null) {
            list.add(category.getId());
        }else {
            return list;
        }
        List<GroupCategory> children = find("parent.id", id).stream().toList();
        for(GroupCategory child : children) {
            delete(child);
            list.add(child.getId());
        }
        delete(category);
        return list;
    }
}
