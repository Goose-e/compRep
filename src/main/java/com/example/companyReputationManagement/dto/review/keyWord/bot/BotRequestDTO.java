package com.example.companyReputationManagement.dto.review.keyWord.bot;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;


public record BotRequestDTO(
        String language,              // "ru"
        List<BotReviewDTO> reviews,   // отзывы
        int maxPoints                 // например 10
) implements Serializable {}
