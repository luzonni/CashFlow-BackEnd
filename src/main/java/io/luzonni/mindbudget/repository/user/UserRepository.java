package io.luzonni.mindbudget.repository.user;

import io.luzonni.mindbudget.domain.model.user.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

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

    public boolean existsByUsername(String username) {
        return count("user.username = ?1", username) > 0;
    }

    public Optional<User> findById(UUID id) {
        return find("id", id).firstResultOptional();
    }

}
