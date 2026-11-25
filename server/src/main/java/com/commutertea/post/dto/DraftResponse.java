package com.commutertea.post.dto;

import com.commutertea.post.PostDraft;

import java.time.LocalDateTime;
import java.util.List;

public record DraftResponse(
        Long id,
        String content,
        String category,
        boolean anonymous,
        List<String> images,
        LocalDateTime updatedAt
) {

    public static DraftResponse from(PostDraft draft) {
        return new DraftResponse(
                draft.getId(), draft.getContent(), draft.getCategory(),
                draft.isAnonymous(), draft.getImages(), draft.getUpdatedAt()
        );
    }
}
