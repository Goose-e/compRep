package com.example.companyReputationManagement.dto.review.keyWord.bot;

import java.io.Serializable;

public record BotReviewDTO(
        String author,
        String text
) implements Serializable {}
