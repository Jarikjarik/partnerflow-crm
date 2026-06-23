package com.yaroslav.partnerflow.partner.controller;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.partner.dto.PartnerResponse;
import com.yaroslav.partnerflow.partner.dto.UpdatePartnerStatusRequest;
import com.yaroslav.partnerflow.partner.service.PartnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<PartnerResponse> findAll() {
        return partnerService.findAll();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PARTNER')")
    public PartnerResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return partnerService.findCurrentPartner(principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public PartnerResponse findById(@PathVariable Long id) {
        return partnerService.findById(id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public PartnerResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePartnerStatusRequest request
    ) {
        return partnerService.updateStatus(id, request);
    }
}