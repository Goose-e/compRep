package com.example.companyReputationManagement.analysis;

import com.example.companyReputationManagement.dto.review.keyWord.bot.EvidenceDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.InsightDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;

import java.util.*;
import java.util.stream.Collectors;

public class InsightBuilder {

    private static final Set<String> STOP = Set.of(
            "и","в","во","на","но","а","я","мы","вы","они","он","она","оно","это","тот","эта",
            "что","как","когда","где","то","же","ли","бы","из","за","по","для","у","о","об",
            "с","со","к","ко","при","до","после","очень","все","всё","тоже","ещё","еще"
    );

    public static List<InsightDTO> toInsights(List<String> reviewIds,
                                              List<String> texts,
                                              List<OnlineClustering.Cluster> clusters,
                                              Sentiment sentiment,
                                              int maxPerCategory,
                                              int quoteMaxLen) {

        clusters.sort((a, b) -> Integer.compare(b.indices.size(), a.indices.size()));

        List<InsightDTO> result = new ArrayList<>();
        for (OnlineClustering.Cluster cl : clusters) {
            if (result.size() >= maxPerCategory) break;

            List<Integer> idxs = cl.indices;
            int count = idxs.size();

            String aspect = buildAspect(texts, idxs);
            if (!isValidAspect(aspect)) {
                continue;
            }
            String statement = buildStatement(aspect, sentiment);

            int pick = idxs.get(0);
            String quote = clip(texts.get(pick), quoteMaxLen);
            EvidenceDTO ev = new EvidenceDTO(reviewIds.get(pick), quote);

            result.add(new InsightDTO(
                    aspect,
                    statement,
                    sentiment,
                    count,
                    List.of(ev)
            ));
        }

        return result.stream()
                .sorted(Comparator.comparingInt(InsightDTO::count).reversed())
                .limit(5)
                .toList();
    }

    private static boolean isValidAspect(String s) {
        if (s == null) return false;
        s = s.trim().toLowerCase();

        if (s.length() < 3) return false;
        if (s.matches("[\\p{Punct}\\d\\s]+")) return false;
        if (Set.of("censored", "unknown", "null").contains(s)) return false;

        return true;
    }

    private static String buildAspect(List<String> texts, List<Integer> idxs) {
        Map<String, Integer> freq = new HashMap<>();

        for (int idx : idxs) {
            for (String token : tokenize(texts.get(idx))) {
                if (token.length() < 4) continue;
                if (STOP.contains(token)) continue;
                freq.merge(token, 1, Integer::sum);
            }
        }

        List<String> top = freq.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(2)
                .collect(Collectors.toList());

        if (top.isEmpty()) return "Общее впечатление";
        if (top.size() == 1) return capitalize(top.get(0));
        return capitalize(top.get(0)) + " / " + top.get(1);
    }

    private static String buildStatement(String aspect, Sentiment sentiment) {
        return switch (sentiment) {
            case POSITIVE -> "Пользователи отмечают: " + aspect + ".";
            case NEGATIVE -> "Пользователи жалуются на: " + aspect + ".";
            case REQUEST -> "Пользователи просят улучшить: " + aspect + ".";
        };
    }

    private static List<String> tokenize(String s) {
        if (s == null) return List.of();
        s = s.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s-]", " ");
        String[] parts = s.split("\\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p.isBlank()) continue;
            out.add(p);
        }
        return out;
    }

    private static String clip(String s, int max) {
        if (s == null) return "";
        s = s.replaceAll("\\s+", " ").trim();
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
