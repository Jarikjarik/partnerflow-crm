package com.yaroslav.partnerflow.deal.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DealResponse(
        Long id,
        Long clientId,
        String clientFullName,
        Long partnerId,
        String partnerEmail,
        Long assignedManagerId,
        String assignedManagerEmail,
        Long statusId,
        String statusCode,
        String statusName,
        String title,
        String propertyName,
        BigDecimal budget,
        BigDecimal amount,
        Long createdById,
        String createdByEmail,
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt
) {
}