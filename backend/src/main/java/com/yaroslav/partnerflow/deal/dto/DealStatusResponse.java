package com.yaroslav.partnerflow.deal.dto;

public record DealStatusResponse(
        Long id,
        String code,
        String name,
        Integer sortOrder,
        boolean finalStatus,
        boolean active
) {
}