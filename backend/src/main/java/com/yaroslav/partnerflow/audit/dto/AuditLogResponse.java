package com.yaroslav.partnerflow.audit.dto;

import java.time.Instant;

public record AuditLogResponse(
        Long id,
        String entityType,
        Long entityId,
        String action,
        String fieldName,
        String oldValue,
        String newValue,
        Long actorId,
        String actorEmail,
        Instant createdAt
) {
}