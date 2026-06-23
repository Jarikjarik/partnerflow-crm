package com.yaroslav.partnerflow.partner.dto;

import java.time.Instant;

public record PartnerResponse(
        Long id,
        Long userId,
        String email,
        String companyName,
        String contactPerson,
        String phone,
        String status,
        Instant createdAt
) {
}