package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.analysis.InsightBuilder;
import com.example.companyReputationManagement.analysis.InsightBuilder.AnchorCategory;
import com.example.companyReputationManagement.analysis.OnlineClustering;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.InsightDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.companyReputationManagement.analysis.InsightBuilder.cosine;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalBotClient {

    private final ExternalEmbeddingClient embeddingClient;
    private volatile Map<AnchorCategory, double[]> anchorEmbeddings;

    @Value("${analysis.cluster.sim-threshold:0.82}")
    private double simThreshold;

    @Value("${analysis.cluster.max-per-category:3}")
    private int maxPerCategory;
    @Value("${analysis.anchor.min-sim:0.32}")
    private double anchorMinSim;

    @Value("${analysis.quote.max-len:120}")
    private int quoteMaxLen;

    @Value("${analysis.text.max-len:600}")
    private int textMaxLen;


    private Map<AnchorCategory, List<Integer>> bucketizeByAnchors(List<double[]> vectors) {
        Map<AnchorCategory, double[]> anchors = getAnchorEmbeddings();
        Map<AnchorCategory, List<Integer>> buckets = new EnumMap<>(AnchorCategory.class);
        for (AnchorCategory c : AnchorCategory.values()) buckets.put(c, new ArrayList<>());

        for (int i = 0; i < vectors.size(); i++) {
            double[] v = vectors.get(i);

            AnchorCategory bestCat = AnchorCategory.OTHER;
            double bestSim = -1;

            for (var e : anchors.entrySet()) {
                AnchorCategory cat = e.getKey();
                if (cat == AnchorCategory.OTHER) continue;

                double sim = cosine(v, e.getValue());
                if (sim > bestSim) {
                    bestSim = sim;
                    bestCat = cat;
                }
            }

            if (bestSim < anchorMinSim) bestCat = AnchorCategory.OTHER;
            buckets.get(bestCat).add(i);
        }

        // выкинем пустые
        buckets.entrySet().removeIf(e -> e.getValue().isEmpty());
        return buckets;
    }


    private Map<AnchorCategory, double[]> getAnchorEmbeddings() {
        var local = anchorEmbeddings;
        if (local != null) return local;

        synchronized (this) {
            if (anchorEmbeddings != null) return anchorEmbeddings;

            Map<AnchorCategory, double[]> m = new EnumMap<>(AnchorCategory.class);
            for (AnchorCategory c : AnchorCategory.values()) {
                // OTHER можно не эмбеддить, но проще эмбеддить всё
                m.put(c, embeddingClient.embed(c.anchorText));
            }
            anchorEmbeddings = m;
            return m;
        }
    }

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

        Map<AnchorCategory, List<Integer>> buckets = bucketizeByAnchors(vectors);

        List<InsightDTO> allInsights = new ArrayList<>();

        for (var entry : buckets.entrySet()) {
            AnchorCategory cat = entry.getKey();
            List<Integer> globalIdxs = entry.getValue();

            // под-векторы
            List<double[]> subVecs = new ArrayList<>(globalIdxs.size());
            for (int gi : globalIdxs) subVecs.add(vectors.get(gi));

            // кластеризация внутри темы
            List<OnlineClustering.Cluster> subClusters = OnlineClustering.cluster(subVecs, simThreshold);

            // маппинг индексов кластера обратно в глобальные
            List<OnlineClustering.Cluster> mapped = new ArrayList<>(subClusters.size());
            for (OnlineClustering.Cluster cl : subClusters) {
                OnlineClustering.Cluster nc = new OnlineClustering.Cluster();
                nc.indices = cl.indices.stream()
                        .map(localIdx -> globalIdxs.get(localIdx))
                        .toList();
                mapped.add(nc);
            }

            // инсайты по теме
            List<InsightDTO> insights = InsightBuilder.toInsights(ids, texts, mapped, sentiment, maxPerCategory, quoteMaxLen);

            // чтобы тема была видна — префиксуем aspect
            insights = insights.stream()
                    .map(in -> new InsightDTO(
                            "[" + cat.title + "] " + in.aspect(),
                            in.statement(),   // можно тоже префикснуть при желании
                            in.sentiment(),
                            in.count(),
                            in.evidence()
                    ))
                    .toList();

            allInsights.addAll(insights);
        }

// финально можно отсортировать и обрезать общий список
        allInsights = allInsights.stream()
                .sorted(Comparator.comparingInt(InsightDTO::count).reversed())
                .limit(maxPerCategory) // если хочешь общий лимит, иначе убери
                .toList();

        return switch (sentiment) {
            case POSITIVE -> new BotResponseDTO(allInsights, List.of(), List.of());
            case NEGATIVE -> new BotResponseDTO(List.of(), allInsights, List.of());
            case REQUEST -> new BotResponseDTO(List.of(), List.of(), allInsights);
        };
    }

    private static String clip(String s, int max) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
