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

    public List<Optional<ReviewInsight>> findLatest(Long companyId, SentimentTypeEnum sentimentType) {
        return reviewInsightRepo.findTopByCompanyIdAndSentimentTypeOrderByCreatedAtDesc(companyId, sentimentType);
    }

    public List<ReviewInsight> findLatestByCompany(Long companyId) {
        List<ReviewInsight> insights = reviewInsightRepo.findAllByCompanyIdOrderByCreatedAtDesc(companyId);
        LinkedHashMap<SentimentTypeEnum, ReviewInsight> latestPerType = new LinkedHashMap<>();

        for (ReviewInsight insight : insights) {
            SentimentTypeEnum sentimentType = insight.getSentimentType();

            if (isSupported(sentimentType) && !latestPerType.containsKey(sentimentType)) {
                latestPerType.put(sentimentType, insight);
            }
        }

        return latestPerType.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> orderByType(entry.getKey())))
                .map(java.util.Map.Entry::getValue)
                .toList();
    }

    private boolean isSupported(SentimentTypeEnum sentimentType) {
        return sentimentType == SentimentTypeEnum.POSITIVE || sentimentType == SentimentTypeEnum.NEGATIVE;
    }

    private int orderByType(SentimentTypeEnum sentimentType) {
        return sentimentType == SentimentTypeEnum.POSITIVE ? 0 : 1;
    }
}
