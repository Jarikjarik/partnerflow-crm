package com.yaroslav.partnerflow.comment.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        Long clientId,
        Long dealId,

        @NotBlank
        @Size(max = 5000)
        String text
) {
    @AssertTrue(message = "Comment must be linked either to clientId or dealId")
    public boolean isTargetValid() {
        return (clientId != null && dealId == null)
                || (clientId == null && dealId != null);
    }
}