package com.yaroslav.partnerflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterPartnerRequest(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotBlank
        @Size(max = 150)
        String fullName,

        @NotBlank
        @Size(max = 150)
        String contactPerson,

        @Size(max = 255)
        String companyName,

        @Size(max = 30)
        String phone
) {
}