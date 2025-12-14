package com.example.companyReputationManagement.dto.review.keyWord.bot;

import java.io.Serializable;

public record SummaryDTO(
        int totalReviews,
        int positiveCount,
        int negativeCount,
        int requestCount
) implements Serializable {
}
