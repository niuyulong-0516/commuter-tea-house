package com.commutertea.post.dto;

import com.commutertea.post.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String content,
        String category,
        String transportType,
        boolean anonymous,
        String authorName,
        List<String> images,
        LocalDateTime createdAt,
        long viewCount
) {

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getCategory().name(),
                post.getTransportType() == null ? "RAIL" : post.getTransportType().name(),
                post.isAnonymous(),
                post.getAuthorName(),
                post.getImages(),
                post.getCreatedAt(),
                post.getViewCount()
        );
    }
}
