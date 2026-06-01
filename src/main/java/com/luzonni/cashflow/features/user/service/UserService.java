package com.luzonni.cashflow.features.user.service;

import com.luzonni.cashflow.features.auth.domain.Role;
import com.luzonni.cashflow.features.auth.repository.RoleRepository;
import com.luzonni.cashflow.features.exception.domain.AppException;
import com.luzonni.cashflow.features.exception.dto.ErrorCode;
import com.luzonni.cashflow.features.settings.dto.SettingsRequest;
import com.luzonni.cashflow.features.settings.service.SettingsService;
import com.luzonni.cashflow.features.user.domain.User;
import com.luzonni.cashflow.features.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final UserRepository repository;
    private final SettingsService settingsService;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository repository,
            SettingsService settingsService,
            RoleRepository roleRepository
    ) {
        this.repository = repository;
        this.settingsService = settingsService;
        this.roleRepository = roleRepository;
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
        Role userRole = roleRepository.findByName("USER"); // TODO consertar essa logica!
        user.getRoles().add(userRole);
        try {
            repository.persist(user);
        }catch (AppException appException){
            throw new AppException(
                 Response.Status.BAD_REQUEST,
                 ErrorCode.INVALID_OPERATION,
                 "User already exists."
            );
        }
        return user;
    }

}
