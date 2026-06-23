package com.yaroslav.partnerflow.partner.service;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.partner.dto.PartnerResponse;
import com.yaroslav.partnerflow.partner.dto.UpdatePartnerStatusRequest;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerProfileRepository partnerProfileRepository;

    @Transactional(readOnly = true)
    public List<PartnerResponse> findAll() {
        return partnerProfileRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartnerResponse findById(Long id) {
        PartnerProfile partner = partnerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + id));

        return toResponse(partner);
    }

    @Transactional(readOnly = true)
    public PartnerResponse findCurrentPartner(UserPrincipal principal) {
        PartnerProfile partner = partnerProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner profile not found"));

        return toResponse(partner);
    }

    @Transactional
    public PartnerResponse updateStatus(Long id, UpdatePartnerStatusRequest request) {
        PartnerProfile partner = partnerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + id));

        partner.setStatus(request.status());

        return toResponse(partner);
    }

    private PartnerResponse toResponse(PartnerProfile partner) {
        return new PartnerResponse(
                partner.getId(),
                partner.getUser().getId(),
                partner.getUser().getEmail(),
                partner.getCompanyName(),
                partner.getContactPerson(),
                partner.getPhone(),
                partner.getStatus().name(),
                partner.getCreatedAt()
        );
    }
}