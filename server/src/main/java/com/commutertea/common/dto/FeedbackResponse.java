package com.commutertea.common.dto;

import com.commutertea.common.Feedback;

import java.time.LocalDateTime;
import java.util.List;

public record FeedbackResponse(
        Long id,
        String description,
        List<String> images,
        String whatsapp,
        LocalDateTime createdAt
) {

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(), feedback.getDescription(), feedback.getImages(),
                feedback.getWhatsapp(), feedback.getCreatedAt()
        );
    }
}
