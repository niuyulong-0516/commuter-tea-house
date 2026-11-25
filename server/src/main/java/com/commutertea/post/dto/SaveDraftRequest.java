package com.commutertea.post.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record SaveDraftRequest(
        @Size(max = 500, message = "内容最多 500 个字")
        String content,
        String category,
        boolean anonymous,
        List<String> images
) {
}
