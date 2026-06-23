package com.yaroslav.partnerflow.partner.dto;

import com.yaroslav.partnerflow.partner.entity.PartnerStatus;
import jakarta.validation.constraints.NotNull;

public record UpdatePartnerStatusRequest(
        @NotNull
        PartnerStatus status
) {
}