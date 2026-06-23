package com.yaroslav.partnerflow.comment.dto;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long clientId,
        Long dealId,
        Long authorId,
        String authorEmail,
        String authorFullName,
        String text,
        Instant createdAt,
        Instant updatedAt
) {
}