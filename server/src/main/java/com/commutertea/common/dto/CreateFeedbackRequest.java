package com.commutertea.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateFeedbackRequest(
        @NotBlank(message = "问题描述不能为空")
        @Size(max = 1000, message = "问题描述最多 1000 个字")
        String description,
        List<String> images,
        @Size(max = 64, message = "WhatsApp 账号最多 64 位")
        String whatsapp
) {
}
