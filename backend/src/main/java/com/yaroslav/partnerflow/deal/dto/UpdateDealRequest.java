package com.yaroslav.partnerflow.deal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateDealRequest(
        @NotBlank
        @Size(max = 200)
        String title,

        @Size(max = 200)
        String propertyName,

        @DecimalMin("0.00")
        BigDecimal budget,

        @DecimalMin("0.00")
        BigDecimal amount,

        Long assignedManagerId
) {
}