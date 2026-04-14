package com.luzonni.cashflow.features.transaction.repository;

import com.luzonni.cashflow.features.transaction.domain.Transaction;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepository implements PanacheRepository<Transaction> {

    public Optional<Transaction> findById(UUID id) {
        return find("id = ?1", id).firstResultOptional();
    }

    public boolean existsByUserCategoryId(UUID categoryId) {
        return find("categoryId = ?1", categoryId).firstResultOptional().isPresent();
    }

    //TODO criar listagem de transações por usuario

}
