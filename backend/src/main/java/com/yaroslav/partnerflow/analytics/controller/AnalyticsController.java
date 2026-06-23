package com.yaroslav.partnerflow.analytics.controller;

import com.yaroslav.partnerflow.analytics.dto.AnalyticsSummaryResponse;
import com.yaroslav.partnerflow.analytics.service.AnalyticsService;
import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public AnalyticsSummaryResponse getSummary(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return analyticsService.getSummary(principal);
    }
}