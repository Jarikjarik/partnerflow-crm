package com.yaroslav.partnerflow.auth.controller;

import com.yaroslav.partnerflow.auth.dto.AuthResponse;
import com.yaroslav.partnerflow.auth.dto.CurrentUserResponse;
import com.yaroslav.partnerflow.auth.dto.LoginRequest;
import com.yaroslav.partnerflow.auth.dto.RegisterPartnerRequest;
import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterPartnerRequest request) {
        return authService.registerPartner(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        return new CurrentUserResponse(
                principal.getId(),
                principal.getEmail(),
                principal.getFullName(),
                principal.getRole()
        );
    }
}