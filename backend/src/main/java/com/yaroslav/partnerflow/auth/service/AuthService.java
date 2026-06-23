package com.yaroslav.partnerflow.auth.service;

import com.yaroslav.partnerflow.auth.dto.AuthResponse;
import com.yaroslav.partnerflow.auth.dto.LoginRequest;
import com.yaroslav.partnerflow.auth.dto.RegisterPartnerRequest;
import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.auth.token.JwtService;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.entity.PartnerStatus;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import com.yaroslav.partnerflow.user.entity.User;
import com.yaroslav.partnerflow.user.entity.UserRole;
import com.yaroslav.partnerflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PartnerProfileRepository partnerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse registerPartner(RegisterPartnerRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(UserRole.PARTNER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        PartnerProfile partnerProfile = new PartnerProfile();
        partnerProfile.setUser(savedUser);
        partnerProfile.setCompanyName(request.companyName());
        partnerProfile.setContactPerson(request.contactPerson());
        partnerProfile.setPhone(request.phone());
        partnerProfile.setStatus(PartnerStatus.PENDING);

        partnerProfileRepository.save(partnerProfile);

        UserPrincipal principal = new UserPrincipal(savedUser);
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);

        return new AuthResponse(
                token,
                "Bearer",
                principal.getId(),
                principal.getEmail(),
                principal.getFullName(),
                principal.getRole()
        );
    }
}