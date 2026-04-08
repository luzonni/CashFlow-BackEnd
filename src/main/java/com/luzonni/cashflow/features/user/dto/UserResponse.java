package com.luzonni.cashflow.features.user.dto;

import com.luzonni.cashflow.features.user.domain.User;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserResponse {

    private String username;
    private String email;
    private LocalDate birthday;
    private LocalDate createdAt;

    public UserResponse(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.birthday = user.getBirthday();
        this.createdAt = user.getCreatedAt().toLocalDate();
    }

}