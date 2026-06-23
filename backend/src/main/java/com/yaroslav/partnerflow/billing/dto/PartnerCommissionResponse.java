package com.yaroslav.partnerflow.billing.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record PartnerCommissionResponse(
        Long id,
        Long partnerId,
        String partnerEmail,
        Long dealId,
        String dealTitle,
        BigDecimal amount,
        BigDecimal percent,
        String status,
        Instant calculatedAt,
        Instant approvedAt,
        Instant paidAt
) {
}