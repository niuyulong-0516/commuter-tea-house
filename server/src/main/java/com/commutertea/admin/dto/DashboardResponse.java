package com.commutertea.admin.dto;

public record DashboardResponse(
        long userCount,
        long postCount,
        long commentCount,
        long feedbackCount
) {
}
