package com.yaroslav.partnerflow.deal.controller;

import com.yaroslav.partnerflow.deal.dto.DealStatusResponse;
import com.yaroslav.partnerflow.deal.service.DealStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deal-statuses")
@RequiredArgsConstructor
public class DealStatusController {

    private final DealStatusService dealStatusService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<DealStatusResponse> findAllActive() {
        return dealStatusService.findAllActive();
    }
}