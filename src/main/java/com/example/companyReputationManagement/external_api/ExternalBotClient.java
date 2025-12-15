package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.analysis.InsightBuilder;
import com.example.companyReputationManagement.analysis.OnlineClustering;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalBotClient {

    private final ExternalEmbeddingClient embeddingClient;

    @Value("${analysis.cluster.sim-threshold:0.82}")
    private double simThreshold;

    @Value("${analysis.cluster.max-per-category:3}")
    private int maxPerCategory;

    @Value("${analysis.quote.max-len:120}")
    private int quoteMaxLen;

    @Value("${analysis.text.max-len:600}")
    private int textMaxLen;

    public BotResponseDTO analyze(BotRequestDTO request) {
        throw new IllegalStateException("Use analyzeFor(request, sentiment) instead");
    }

    public BotResponseDTO analyzeFor(BotRequestDTO request, Sentiment sentiment) {
        List<String> ids = new ArrayList<>();
        List<String> texts = new ArrayList<>();

        for (int i = 0; i < request.reviews().size(); i++) {
            var r = request.reviews().get(i);
            ids.add("review-" + (i + 1));
            texts.add(clip(r.text(), textMaxLen));
        }

        if (texts.isEmpty()) {
            return new BotResponseDTO(List.of(), List.of(), List.of());
        }

        List<double[]> vectors = new ArrayList<>(texts.size());
        for (String t : texts) {
            vectors.add(embeddingClient.embed(t));
        }

        List<OnlineClustering.Cluster> clusters = OnlineClustering.cluster(vectors, simThreshold);
        var insights = InsightBuilder.toInsights(ids, texts, clusters, sentiment, maxPerCategory, quoteMaxLen);

        return switch (sentiment) {
            case POSITIVE -> new BotResponseDTO(insights, List.of(), List.of());
            case NEGATIVE -> new BotResponseDTO(List.of(), insights, List.of());
            case REQUEST -> new BotResponseDTO(List.of(), List.of(), insights);
        };
    }

    private static String clip(String s, int max) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
