package com.commutertea.user.dto;

import java.time.LocalDateTime;
import java.util.List;

/** 背景图列表：当前使用的一张 + 用户用过的历史。 */
public record BackgroundResponse(
        String current,
        List<Item> items
) {

    public record Item(Long id, String url, LocalDateTime createdAt) {
    }
}
