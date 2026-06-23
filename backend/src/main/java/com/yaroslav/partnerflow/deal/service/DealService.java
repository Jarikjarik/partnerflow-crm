package com.yaroslav.partnerflow.deal.service;

import com.yaroslav.partnerflow.audit.service.AuditService;
import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.billing.event.BillingEventPublisher;
import com.yaroslav.partnerflow.client.entity.Client;
import com.yaroslav.partnerflow.client.repository.ClientRepository;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.deal.dto.CreateDealRequest;
import com.yaroslav.partnerflow.deal.dto.DealResponse;
import com.yaroslav.partnerflow.deal.dto.UpdateDealRequest;
import com.yaroslav.partnerflow.deal.dto.UpdateDealStatusRequest;
import com.yaroslav.partnerflow.deal.entity.Deal;
import com.yaroslav.partnerflow.deal.entity.DealStatus;
import com.yaroslav.partnerflow.deal.repository.DealRepository;
import com.yaroslav.partnerflow.deal.repository.DealStatusRepository;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import com.yaroslav.partnerflow.user.entity.User;
import com.yaroslav.partnerflow.user.entity.UserRole;
import com.yaroslav.partnerflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealService {

    private static final String DEFAULT_STATUS_CODE = "NEW";

    private final DealRepository dealRepository;
    private final DealStatusRepository dealStatusRepository;
    private final ClientRepository clientRepository;
    private final PartnerProfileRepository partnerProfileRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final BillingEventPublisher billingEventPublisher;

    @Transactional(readOnly = true)
    public List<DealResponse> findAll(UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return dealRepository.findByPartner_Id(partner.getId())
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return dealRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DealResponse findById(Long id, UserPrincipal principal) {
        Deal deal = findDealForCurrentUser(id, principal);
        return toResponse(deal);
    }

    @Transactional
    public DealResponse create(CreateDealRequest request, UserPrincipal principal) {
        User createdBy = getUser(principal.getId());
        Client client = getClientForCreate(request.clientId(), principal);
        DealStatus status = getStatus(DEFAULT_STATUS_CODE);

        Deal deal = new Deal();
        deal.setClient(client);
        deal.setPartner(client.getPartner());
        deal.setAssignedManager(resolveAssignedManager(request.assignedManagerId(), principal));
        deal.setStatus(status);
        deal.setTitle(request.title());
        deal.setPropertyName(request.propertyName());
        deal.setBudget(request.budget());
        deal.setAmount(request.amount());
        deal.setCreatedBy(createdBy);

        Deal savedDeal = dealRepository.save(deal);

        auditService.log(
                "DEAL",
                savedDeal.getId(),
                "CREATED",
                null,
                null,
                savedDeal.getTitle(),
                principal.getId()
        );

        return toResponse(savedDeal);
    }

    @Transactional
    public DealResponse update(Long id, UpdateDealRequest request, UserPrincipal principal) {
        Deal deal = findDealForCurrentUser(id, principal);

        String oldTitle = deal.getTitle();
        deal.setTitle(request.title());
        deal.setPropertyName(request.propertyName());
        deal.setBudget(request.budget());
        deal.setAmount(request.amount());

        if (!isPartner(principal)) {
            deal.setAssignedManager(
                    request.assignedManagerId() == null ? null : getUser(request.assignedManagerId())
            );
        }

        if (!oldTitle.equals(deal.getTitle())) {
            auditService.log("DEAL", deal.getId(), "UPDATED", "title", oldTitle, deal.getTitle(), principal.getId());
        }

        return toResponse(deal);
    }

    @Transactional
    public DealResponse updateStatus(Long id, UpdateDealStatusRequest request, UserPrincipal principal) {
        if (isPartner(principal)) {
            throw new AccessDeniedException("Partners cannot change deal status");
        }

        Deal deal = findDealForCurrentUser(id, principal);
        DealStatus newStatus = getStatus(request.statusCode());

        String oldStatusCode = deal.getStatus().getCode();
        deal.setStatus(newStatus);

        if (newStatus.isFinalStatus()) {
            deal.setClosedAt(Instant.now());
        } else {
            deal.setClosedAt(null);
        }

        auditService.log(
                "DEAL",
                deal.getId(),
                "STATUS_CHANGED",
                "status",
                oldStatusCode,
                newStatus.getCode(),
                principal.getId()
        );

        if ("WON".equalsIgnoreCase(newStatus.getCode())
                && !"WON".equalsIgnoreCase(oldStatusCode)) {
            billingEventPublisher.publishDealWon(deal.getId(), principal.getId());
        }

        return toResponse(deal);
    }

    private Deal findDealForCurrentUser(Long id, UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return dealRepository.findByIdAndPartner_Id(id, partner.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
        }

        return dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + id));
    }

    private Client getClientForCreate(Long clientId, UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return clientRepository.findByPartner_IdAndIdAndArchivedFalse(partner.getId(), clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        }

        return clientRepository.findByIdAndArchivedFalse(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
    }

    private User resolveAssignedManager(Long assignedManagerId, UserPrincipal principal) {
        if (isPartner(principal)) {
            return null;
        }

        if (assignedManagerId == null) {
            return null;
        }

        User user = getUser(assignedManagerId);

        if (user.getRole() != UserRole.MANAGER) {
            throw new AccessDeniedException("Assigned user must have MANAGER role");
        }

        return user;
    }

    private DealStatus getStatus(String code) {
        return dealStatusRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Deal status not found: " + code));
    }

    private PartnerProfile getCurrentPartner(UserPrincipal principal) {
        return partnerProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new AccessDeniedException("Partner profile not found"));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private boolean isPartner(UserPrincipal principal) {
        return UserRole.PARTNER.name().equals(principal.getRole());
    }

    private DealResponse toResponse(Deal deal) {
        Client client = deal.getClient();
        PartnerProfile partner = deal.getPartner();
        User assignedManager = deal.getAssignedManager();
        User createdBy = deal.getCreatedBy();
        DealStatus status = deal.getStatus();

        return new DealResponse(
                deal.getId(),
                client.getId(),
                client.getFullName(),
                partner == null ? null : partner.getId(),
                partner == null ? null : partner.getUser().getEmail(),
                assignedManager == null ? null : assignedManager.getId(),
                assignedManager == null ? null : assignedManager.getEmail(),
                status.getId(),
                status.getCode(),
                status.getName(),
                deal.getTitle(),
                deal.getPropertyName(),
                deal.getBudget(),
                deal.getAmount(),
                createdBy.getId(),
                createdBy.getEmail(),
                deal.getCreatedAt(),
                deal.getUpdatedAt(),
                deal.getClosedAt()
        );
    }
}