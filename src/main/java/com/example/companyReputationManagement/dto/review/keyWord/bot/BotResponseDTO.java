package com.example.companyReputationManagement.dto.review.keyWord.bot;

import com.example.companyReputationManagement.httpResponse.ResponseDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;


import java.io.Serializable;
import java.util.List;

public record BotResponseDTO(
        List<InsightDTO> topLikes,
        List<InsightDTO> topDislikes,
        List<InsightDTO> topRequests
) implements Serializable, ResponseDto {
}

