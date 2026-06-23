package com.yaroslav.partnerflow.comment.controller;

import com.yaroslav.partnerflow.auth.security.UserPrincipal;
import com.yaroslav.partnerflow.comment.dto.CommentResponse;
import com.yaroslav.partnerflow.comment.dto.CreateCommentRequest;
import com.yaroslav.partnerflow.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public CommentResponse create(
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return commentService.create(request, principal);
    }

    @GetMapping("/clients/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<CommentResponse> findByClientId(
            @PathVariable Long clientId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return commentService.findByClientId(clientId, principal);
    }

    @GetMapping("/deals/{dealId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PARTNER')")
    public List<CommentResponse> findByDealId(
            @PathVariable Long dealId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return commentService.findByDealId(dealId, principal);
    }
}