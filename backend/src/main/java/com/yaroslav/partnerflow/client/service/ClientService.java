package com.yaroslav.partnerflow.client.service;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.client.dto.ClientResponse;
import com.yaroslav.partnerflow.client.dto.CreateClientRequest;
import com.yaroslav.partnerflow.client.dto.UpdateClientRequest;
import com.yaroslav.partnerflow.client.entity.Client;
import com.yaroslav.partnerflow.client.repository.ClientRepository;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.partner.entity.PartnerProfile;
import com.yaroslav.partnerflow.partner.repository.PartnerProfileRepository;
import com.yaroslav.partnerflow.user.entity.User;
import com.yaroslav.partnerflow.user.entity.UserRole;
import com.yaroslav.partnerflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final PartnerProfileRepository partnerProfileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ClientResponse> findAll(UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return clientRepository.findByPartner_IdAndArchivedFalse(partner.getId())
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        return clientRepository.findByArchivedFalse()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id, UserPrincipal principal) {
        Client client = findClientForCurrentUser(id, principal);
        return toResponse(client);
    }

    @Transactional
    public ClientResponse create(CreateClientRequest request, UserPrincipal principal) {
        User createdBy = getUser(principal.getId());

        Client client = new Client();
        client.setFullName(request.fullName());
        client.setPhone(request.phone());
        client.setEmail(request.email());
        client.setSource(request.source());
        client.setCreatedBy(createdBy);

        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);
            client.setPartner(partner);
        } else {
            if (request.partnerId() != null) {
                client.setPartner(getPartner(request.partnerId()));
            }

            if (request.assignedManagerId() != null) {
                client.setAssignedManager(getUser(request.assignedManagerId()));
            }
        }

        return toResponse(clientRepository.save(client));
    }

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest request, UserPrincipal principal) {
        Client client = findClientForCurrentUser(id, principal);

        client.setFullName(request.fullName());
        client.setPhone(request.phone());
        client.setEmail(request.email());
        client.setSource(request.source());

        if (!isPartner(principal)) {
            client.setPartner(request.partnerId() == null ? null : getPartner(request.partnerId()));
            client.setAssignedManager(request.assignedManagerId() == null ? null : getUser(request.assignedManagerId()));
        }

        return toResponse(client);
    }

    @Transactional
    public void archive(Long id, UserPrincipal principal) {
        Client client = findClientForCurrentUser(id, principal);
        client.setArchived(true);
    }

    private Client findClientForCurrentUser(Long id, UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return clientRepository.findByPartner_IdAndIdAndArchivedFalse(partner.getId(), id)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        }

        return clientRepository.findByIdAndArchivedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    private PartnerProfile getCurrentPartner(UserPrincipal principal) {
        return partnerProfileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new AccessDeniedException("Partner profile not found"));
    }

    private PartnerProfile getPartner(Long id) {
        return partnerProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner not found with id: " + id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private boolean isPartner(UserPrincipal principal) {
        return UserRole.PARTNER.name().equals(principal.getRole());
    }

    private ClientResponse toResponse(Client client) {
        PartnerProfile partner = client.getPartner();
        User assignedManager = client.getAssignedManager();
        User createdBy = client.getCreatedBy();

        return new ClientResponse(
                client.getId(),
                client.getFullName(),
                client.getPhone(),
                client.getEmail(),
                client.getSource(),
                partner == null ? null : partner.getId(),
                partner == null ? null : partner.getUser().getEmail(),
                assignedManager == null ? null : assignedManager.getId(),
                assignedManager == null ? null : assignedManager.getEmail(),
                createdBy.getId(),
                createdBy.getEmail(),
                client.isArchived(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }
}