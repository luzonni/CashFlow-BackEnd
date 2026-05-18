package com.luzonni.cashflow.features.user.service;

import com.luzonni.cashflow.features.settings.dto.SettingsRequest;
import com.luzonni.cashflow.features.settings.service.SettingsService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;
    private final SettingsService settingsService;

    public UserService(
            UserRepository repository,
            SettingsService settingsService
    ) {
        this.repository = repository;
        this.settingsService = settingsService;
    }

    @Transactional
    public void changeSettings(UUID userId, SettingsRequest request) {
        User user = repository.findById(userId).orElseThrow();
        settingsService.change(user, request);
    }

    @Transactional
    public User create(String username, String email, LocalDate birthday, String password) {
        User user = new User(
                username,
                email,
                birthday,
                password
        );
        repository.persist(user);
        return user;
    }

}
