package com.yaroslav.partnerflow.analytics.dto;

import java.math.BigDecimal;

public record AnalyticsSummaryResponse(
        long totalClients,
        long totalDeals,
        long wonDeals,
        long lostDeals,
        BigDecimal totalDealAmount,
        BigDecimal wonDealAmount,
        BigDecimal calculatedCommissionAmount
) {
}