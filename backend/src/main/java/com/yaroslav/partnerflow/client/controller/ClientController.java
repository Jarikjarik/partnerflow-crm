package com.yaroslav.partnerflow.client.controller;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.client.dto.ClientResponse;
import com.yaroslav.partnerflow.client.dto.CreateClientRequest;
import com.yaroslav.partnerflow.client.dto.UpdateClientRequest;
import com.yaroslav.partnerflow.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<ClientResponse> findAll(@AuthenticationPrincipal UserPrincipal principal) {
        return clientService.findAll(principal);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public ClientResponse findById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return clientService.findById(id, principal);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public ClientResponse create(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return clientService.create(request, principal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public ClientResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClientRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return clientService.update(id, request, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public void archive(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        clientService.archive(id, principal);
    }
}