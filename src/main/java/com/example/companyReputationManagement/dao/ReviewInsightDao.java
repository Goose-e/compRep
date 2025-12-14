package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.ReviewInsight;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.repo.ReviewInsightRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReviewInsightDao {
    private final ReviewInsightRepo reviewInsightRepo;

    public ReviewInsight save(ReviewInsight insight) {
        return reviewInsightRepo.save(insight);
    }

    public Optional<ReviewInsight> findLatest(Long companyId, SentimentTypeEnum sentimentType) {
        return reviewInsightRepo.findTopByCompanyIdAndSentimentTypeOrderByCreatedAtDesc(companyId, sentimentType);
    }
}
