package com.commutertea.common.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateAgreementRequest(
        @NotBlank(message = "协议内容不能为空")
        String content
) {
}
