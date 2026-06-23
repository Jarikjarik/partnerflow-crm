package com.yaroslav.partnerflow.client.dto;

import java.time.Instant;

public record ClientResponse(
        Long id,
        String fullName,
        String phone,
        String email,
        String source,
        Long partnerId,
        String partnerEmail,
        Long assignedManagerId,
        String assignedManagerEmail,
        Long createdById,
        String createdByEmail,
        boolean archived,
        Instant createdAt,
        Instant updatedAt
) {
}