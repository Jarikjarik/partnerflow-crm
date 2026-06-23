package com.yaroslav.partnerflow.comment.service;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.client.entity.Client;
import com.yaroslav.partnerflow.client.repository.ClientRepository;
import com.yaroslav.partnerflow.comment.dto.CommentResponse;
import com.yaroslav.partnerflow.comment.dto.CreateCommentRequest;
import com.yaroslav.partnerflow.comment.entity.Comment;
import com.yaroslav.partnerflow.comment.repository.CommentRepository;
import com.yaroslav.partnerflow.common.exception.ResourceNotFoundException;
import com.yaroslav.partnerflow.deal.entity.Deal;
import com.yaroslav.partnerflow.deal.repository.DealRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final ClientRepository clientRepository;
    private final DealRepository dealRepository;
    private final UserRepository userRepository;
    private final PartnerProfileRepository partnerProfileRepository;

    @Transactional
    public CommentResponse create(CreateCommentRequest request, UserPrincipal principal) {
        User author = getUser(principal.getId());

        Comment comment = new Comment();
        comment.setAuthor(author);
        comment.setText(request.text());

        if (request.clientId() != null) {
            Client client = getClientForCurrentUser(request.clientId(), principal);
            comment.setClient(client);
        }

        if (request.dealId() != null) {
            Deal deal = getDealForCurrentUser(request.dealId(), principal);
            comment.setDeal(deal);
        }

        return toResponse(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByClientId(Long clientId, UserPrincipal principal) {
        getClientForCurrentUser(clientId, principal);

        return commentRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> findByDealId(Long dealId, UserPrincipal principal) {
        getDealForCurrentUser(dealId, principal);

        return commentRepository.findByDealIdOrderByCreatedAtDesc(dealId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Client getClientForCurrentUser(Long clientId, UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return clientRepository.findByPartner_IdAndIdAndArchivedFalse(partner.getId(), clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        }

        return clientRepository.findByIdAndArchivedFalse(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
    }

    private Deal getDealForCurrentUser(Long dealId, UserPrincipal principal) {
        if (isPartner(principal)) {
            PartnerProfile partner = getCurrentPartner(principal);

            return dealRepository.findByIdAndPartner_Id(dealId, partner.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));
        }

        return dealRepository.findById(dealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found with id: " + dealId));
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

    private CommentResponse toResponse(Comment comment) {
        User author = comment.getAuthor();

        return new CommentResponse(
                comment.getId(),
                comment.getClient() == null ? null : comment.getClient().getId(),
                comment.getDeal() == null ? null : comment.getDeal().getId(),
                author.getId(),
                author.getEmail(),
                author.getFullName(),
                comment.getText(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}