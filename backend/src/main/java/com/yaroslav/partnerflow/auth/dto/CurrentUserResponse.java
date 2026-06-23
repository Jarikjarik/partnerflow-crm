package com.yaroslav.partnerflow.auth.dto;

public record CurrentUserResponse(
        Long id,
        String email,
        String fullName,
        String role
) {
}