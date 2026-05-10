package com.luzonni.cashflow.features.settings.service;

import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.settings.dto.SettingsRequest;
import com.luzonni.cashflow.features.settings.repository.SettingsRepository;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class SettingsService {

    private final SettingsRepository repository;
    private final UserRepository userRepository;

    public SettingsService(
            SettingsRepository settingsRepository,
            UserRepository userRepository
    ) {
        this.repository = settingsRepository;
        this.userRepository = userRepository;
    }

    public Settings get(UUID userId) {
        Settings settings = repository.getById(userId);
        if(settings == null) {
            settings = create(userId);
        }
        return settings;
    }

    @Transactional
    public Settings create(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Settings settings = new Settings();
        settings.setUser(user);
        repository.persist(settings);
        return settings;
    }

    @Transactional
    public void change(User user, SettingsRequest request) {
        Settings settings = repository.getById(user.getId());
        if(settings == null) {
            throw new NotFoundException("The settings not exists!");
        }
        if(request.getCurrency() != null)
            settings.setCurrency(request.getCurrency());
        if(request.getTheme() != null)
            settings.setTheme(request.getTheme());
        if(request.getLocale() != null)
            settings.setLocale(request.getLocale());
        repository.persist(settings);
    }

}
