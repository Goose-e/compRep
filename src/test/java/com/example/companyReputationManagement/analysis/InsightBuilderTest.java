package com.example.companyReputationManagement.analysis;

import com.example.companyReputationManagement.dto.review.keyWord.bot.InsightDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InsightBuilderTest {

    @Test
    void returnsInsightEvenForSingleReviewCluster() {
        var clusters = List.of(new OnlineClustering.Cluster());
        clusters.getFirst().indices.add(0);
        clusters.getFirst().centroid = new double[]{1.0, 0.0};

        List<String> ids = List.of("review-1");
        List<String> texts = List.of("Отличная поддержка клиентов и быстрая доставка");

        List<InsightDTO> insights = InsightBuilder.toInsights(
                ids,
                texts,
                clusters,
                Sentiment.POSITIVE,
                3,
                120
        );

        assertThat(insights)
                .hasSize(1)
                .allSatisfy(insight -> {
                    assertThat(insight.aspect()).isNotBlank();
                    assertThat(insight.count()).isEqualTo(1);
                    assertThat(insight.sentiment()).isEqualTo(Sentiment.POSITIVE);
                });
    }
}
