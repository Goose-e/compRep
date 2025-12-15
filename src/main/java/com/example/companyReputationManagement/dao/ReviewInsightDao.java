package com.example.companyReputationManagement.dao;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.models.ReviewInsight;
import com.example.companyReputationManagement.models.enums.SentimentTypeEnum;
import com.example.companyReputationManagement.repo.ReviewInsightRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ReviewInsightDao {
    private final ReviewInsightRepo reviewInsightRepo;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public ReviewInsight save(ReviewInsight insight) {
        return reviewInsightRepo.save(insight);
    }

    public Optional<ReviewInsight> findLatest(Long companyId, SentimentTypeEnum sentimentType) {
        Optional<ReviewInsight> res =
                reviewInsightRepo.findTopByCompanyIdAndSentimentTypeOrderByCreatedAtDesc(companyId, sentimentType);

        log.info("DB findLatest: companyId={}, sentiment={}, present={}",
                companyId, sentimentType, res.isPresent());

        res.ifPresent(ri -> log.info(
                "DB result: id={}, createdAt={}, likes={}, dislikes={}, requests={}",
                ri.getId(),
                ri.getCreatedAt(),
                size(ri.getResultJson().topLikes()),
                size(ri.getResultJson().topDislikes()),
                size(ri.getResultJson().topRequests())
        ));

        return res;
    }

    private int size(List<?> l) {
        return l == null ? 0 : l.size();
    }


    public List<ReviewInsight> findLatestByCompany(Long companyId) {
        return Arrays.stream(SentimentTypeEnum.values())
                .filter(this::isSupported)
                .map(type -> reviewInsightRepo
                        .findTopByCompanyIdAndSentimentTypeOrderByCreatedAtDesc(companyId, type))
                .flatMap(Optional::stream)
                .filter(this::hasInsightContent)
                .sorted(Comparator.comparingInt(i -> orderByType(i.getSentimentType())))
                .toList();
    }

    private boolean hasInsightContent(ReviewInsight insight) {
        BotResponseDTO response = insight.getResultJson();

        return response != null && ((response.topLikes() != null && !response.topLikes().isEmpty())
                || (response.topDislikes() != null && !response.topDislikes().isEmpty())
                || (response.topRequests() != null && !response.topRequests().isEmpty()));
    }

    private boolean isSupported(SentimentTypeEnum sentimentType) {
        return sentimentType == SentimentTypeEnum.POSITIVE || sentimentType == SentimentTypeEnum.NEGATIVE;
    }

    private int orderByType(SentimentTypeEnum sentimentType) {
        return sentimentType == SentimentTypeEnum.POSITIVE ? 0 : 1;
    }
}
