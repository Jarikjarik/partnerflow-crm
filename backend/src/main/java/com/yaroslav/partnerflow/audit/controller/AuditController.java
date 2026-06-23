package com.yaroslav.partnerflow.audit.controller;

import com.yaroslav.partnerflow.audit.dto.AuditLogResponse;
import com.yaroslav.partnerflow.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<AuditLogResponse> findByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId
    ) {
        return auditService.findByEntity(entityType.toUpperCase(), entityId);
    }
}