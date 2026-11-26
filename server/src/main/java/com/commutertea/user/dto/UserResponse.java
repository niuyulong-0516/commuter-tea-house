package com.commutertea.user.dto;

import com.commutertea.user.User;
import com.commutertea.user.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String username,
        String transportType,
        String token,
        LocalDateTime createdAt,
        String avatar,
        String backgroundImage,
        String nickname,
        String gender,
        String bio,
        String role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getDisplayId(),
                user.getUsername(),
                user.getTransportType().name(),
                user.getToken(),
                user.getCreatedAt(),
                user.getAvatar(),
                user.getBackgroundImage(),
                user.getNickname(),
                user.getGender(),
                user.getBio(),
                user.getRole() == null ? UserRole.USER.name() : user.getRole().name()
        );
    }
}
