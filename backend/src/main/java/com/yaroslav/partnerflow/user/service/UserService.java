package com.yaroslav.partnerflow.user.service;

import com.yaroslav.partnerflow.common.exception.EmailAlreadyExistsException;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.user.dto.CreateManagerRequest;
import com.yaroslav.partnerflow.user.dto.UpdateUserStatusRequest;
import com.yaroslav.partnerflow.user.dto.UserResponse;
import com.yaroslav.partnerflow.user.entity.User;
import com.yaroslav.partnerflow.user.entity.UserRole;
import com.yaroslav.partnerflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse createManager(CreateManagerRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User manager = new User();
        manager.setEmail(request.email());
        manager.setPasswordHash(passwordEncoder.encode(request.password()));
        manager.setFullName(request.fullName());
        manager.setRole(UserRole.MANAGER);
        manager.setEnabled(true);

        return toResponse(userRepository.save(manager));
    }

    @Transactional
    public UserResponse updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(request.enabled());

        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.isEnabled(),
                user.getCreatedAt()
        );
    }
}