package com.example.companyReputationManagement.dto.review.keyWord.bot;

import java.io.Serializable;

public record EvidenceDTO(
        String reviewId,
        String quote
) implements Serializable {
}

