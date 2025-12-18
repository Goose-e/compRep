package com.example.companyReputationManagement.dto.review.keyWord.bot;

import com.example.companyReputationManagement.models.enums.Sentiment;

import java.io.Serializable;

import java.util.List;



public record InsightDTO(
        String aspect,
        String statement,
        Sentiment sentiment,
        int count,
        List<EvidenceDTO> evidence
) implements Serializable {
}



