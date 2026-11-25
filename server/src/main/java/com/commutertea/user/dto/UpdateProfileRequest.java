package com.commutertea.user.dto;

public record UpdateProfileRequest(
        String avatar,
        String nickname,
        String gender,
        String bio
) {
}
