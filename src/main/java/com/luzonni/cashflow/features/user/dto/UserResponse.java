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

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setBirthday(user.getBirthday());
        userResponse.setCreatedAt(user.getCreatedAt().toLocalDate());
        return userResponse;
    }

}
