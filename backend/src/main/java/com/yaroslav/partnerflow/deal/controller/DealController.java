package com.yaroslav.partnerflow.deal.controller;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.deal.dto.CreateDealRequest;
import com.yaroslav.partnerflow.deal.dto.DealResponse;
import com.yaroslav.partnerflow.deal.dto.UpdateDealRequest;
import com.yaroslav.partnerflow.deal.dto.UpdateDealStatusRequest;
import com.yaroslav.partnerflow.deal.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<DealResponse> findAll(@AuthenticationPrincipal UserPrincipal principal) {
        return dealService.findAll(principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public DealResponse findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dealService.findById(id, principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public DealResponse create(
            @Valid @RequestBody CreateDealRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dealService.create(request, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public DealResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDealRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dealService.update(id, request, principal);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public DealResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDealStatusRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return dealService.updateStatus(id, request, principal);
    }
}