package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.models.ReviewInsight;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.repo.ReviewInsightRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
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

    public List<ReviewInsight> findLatestByCompany(Long companyId) {
        List<ReviewInsight> insights = reviewInsightRepo.findAllByCompanyIdOrderByCreatedAtDesc(companyId);
        LinkedHashMap<SentimentTypeEnum, ReviewInsight> latestPerType = new LinkedHashMap<>();

        for (ReviewInsight insight : insights) {
            latestPerType.putIfAbsent(insight.getSentimentType(), insight);
        }

        return latestPerType.values().stream()
                .sorted((a, b) -> Integer.compare(orderByType(a.getSentimentType()), orderByType(b.getSentimentType())))
                .toList();
    }

    private int orderByType(SentimentTypeEnum sentimentType) {
        return switch (sentimentType) {
            case POSITIVE -> 0;
            case NEGATIVE -> 1;
            default -> 2;
        };
    }
}
