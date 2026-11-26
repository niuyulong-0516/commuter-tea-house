package com.commutertea.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** 后台发送官方消息。userId 为空表示发给所有用户。 */
public record SendNotificationRequest(
        @NotBlank @Size(max = 60) String title,
        @NotBlank @Size(max = 500) String content,
        Long userId
) {
}
