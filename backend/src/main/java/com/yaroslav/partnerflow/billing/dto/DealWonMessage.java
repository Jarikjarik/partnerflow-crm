package com.yaroslav.partnerflow.billing.dto;

import java.time.Instant;

public record DealWonMessage(
        Long dealId,
        Long actorId,
        Instant occurredAt
) {
}