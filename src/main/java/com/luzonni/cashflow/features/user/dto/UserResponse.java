package com.luzonni.cashflow.features.user.dto;

import com.luzonni.cashflow.features.authorization.domain.Role;
import com.luzonni.cashflow.features.settings.domain.Settings;
import com.luzonni.cashflow.features.settings.dto.SettingsResponse;
import com.luzonni.cashflow.features.user.domain.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponse {

    private String username;
    private String email;
    private List<String> roles;
    private LocalDate birthday;
    private SettingsResponse settings;
    private LocalDate createdAt;

    public UserResponse(User user, Settings settings) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
        this.settings = new SettingsResponse(settings);
        this.roles = user.getRoles().stream().map((Role::getName)).toList();
        this.createdAt = user.getCreatedAt().toLocalDate();
    }

}