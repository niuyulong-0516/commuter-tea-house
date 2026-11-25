package com.commutertea.common.dto;

import com.commutertea.common.Agreement;

import java.time.LocalDateTime;

public record AgreementResponse(
        String code,
        String content,
        LocalDateTime updatedAt
) {

    public static AgreementResponse from(Agreement agreement) {
        return new AgreementResponse(agreement.getCode(), agreement.getContent(), agreement.getUpdatedAt());
    }
}
