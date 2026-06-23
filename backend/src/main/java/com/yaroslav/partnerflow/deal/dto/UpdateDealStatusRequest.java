package com.yaroslav.partnerflow.deal.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDealStatusRequest(
        @NotBlank
        String statusCode
) {
}