package io.luzonni.mindbudget.rest.dto.user;

import io.luzonni.mindbudget.domain.model.user.User;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private LocalDate birthday;
    private LocalDate createdAt;

    public static UserResponse from(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setBirthday(user.getBirthday());
        userResponse.setCreatedAt(user.getCreatedAt().toLocalDate());
        return userResponse;
    }

}
