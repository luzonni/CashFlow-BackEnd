package com.luzonni.cashflow.features.user.repository;

import com.luzonni.cashflow.features.user.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public Optional<User> findById(UUID id) {
        return find("id", id).firstResultOptional();
    }

    public User getUserById(UUID id) {
        Optional<User> option = findById(id);
        if(option.isPresent()) {
            return option.get();
        }
        throw new NotFoundException("User not found");
    }

}
