package com.commutertea.banner.dto;

public record BannerResponse(
        Long postId,
        String content,
        String image
) {
}
