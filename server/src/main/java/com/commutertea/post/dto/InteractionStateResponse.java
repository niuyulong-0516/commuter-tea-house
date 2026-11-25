package com.commutertea.post.dto;

public record InteractionStateResponse(
        boolean liked,
        boolean favorited,
        long likeCount,
        long favoriteCount,
        long commentCount
) {
}
