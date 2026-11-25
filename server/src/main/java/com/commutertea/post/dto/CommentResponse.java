package com.commutertea.post.dto;

import com.commutertea.post.PostComment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long userId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {

    public static CommentResponse from(PostComment comment) {
        return new CommentResponse(
                comment.getId(), comment.getPostId(), comment.getUserId(),
                comment.getAuthorName(), comment.getContent(), comment.getCreatedAt()
        );
    }
}
