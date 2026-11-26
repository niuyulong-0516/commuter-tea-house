package com.commutertea.notification.dto;

import com.commutertea.notification.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String type,
        String title,
        String content,
        Long postId,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getType(), n.getTitle(), n.getContent(),
                n.getPostId(), n.isRead(), n.getCreatedAt()
        );
    }
}
