package com.yaroslav.partnerflow.user.controller;

import com.yaroslav.partnerflow.user.dto.CreateManagerRequest;
import com.yaroslav.partnerflow.user.dto.UpdateUserStatusRequest;
import com.yaroslav.partnerflow.user.dto.UserResponse;
import com.yaroslav.partnerflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @PostMapping("/managers")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createManager(@Valid @RequestBody CreateManagerRequest request) {
        return userService.createManager(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateUserStatusRequest request
    ) {
        return userService.updateStatus(id, request);
    }
}