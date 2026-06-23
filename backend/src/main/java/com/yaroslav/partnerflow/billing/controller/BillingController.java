package com.yaroslav.partnerflow.billing.controller;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.billing.dto.PartnerCommissionResponse;
import com.yaroslav.partnerflow.billing.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService billingService;

    @GetMapping("/commissions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<PartnerCommissionResponse> findCommissions(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return billingService.findCommissions(principal);
    }
}