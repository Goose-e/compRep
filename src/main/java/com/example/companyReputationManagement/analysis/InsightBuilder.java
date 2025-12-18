package com.example.companyReputationManagement.analysis;

import com.example.companyReputationManagement.dto.review.keyWord.bot.EvidenceDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.InsightDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;

import java.util.*;
import java.util.stream.Collectors;
import java.util.*;
import java.util.stream.Collectors;

public class InsightBuilder {

    private static final Set<String> STOP = Set.of(
            "и","в","во","на","но","а","я","мы","вы","они","он","она","оно","это","тот","эта",
            "что","как","когда","где","то","же","ли","бы","из","за","по","для","у","о","об",
            "с","со","к","ко","при","до","после","очень","все","всё","тоже","ещё","еще",
            "вообще","просто","там","тут","здесь","сразу","потом","тогда","который","которая","которые",
            "потому","поэтому","типа","вроде","ну","вот","да","нет"
    );
    public enum AnchorCategory {
        DELIVERY("Доставка", "доставка, курьер, сроки, привезли, опоздал, пункт выдачи, транспортировка, ожидание, задержка"),
        PRICE("Цена", "цена, стоимость, дорого, дёшево, скидка, акция, переплата, ценник"),
        SUPPORT("Поддержка", "служба поддержки, оператор, чат, звонок, отвечают, помощь, решение проблемы, саппорт"),
        QUALITY("Качество", "качество, брак, дефект, сломалось, неисправность, надежность, плохая сборка"),
        PACKAGING("Упаковка", "упаковка, коробка, пломба, мятая, повреждена, целостность, упаковочный материал"),
        UX("Интерфейс", "приложение, интерфейс, удобно, неудобно, дизайн, кнопки, навигация, баг, тормозит"),
        FEATURES("Функции", "функции, возможности, настройки, фича, режим, не хватает, добавить, улучшить"),
        OTHER("Другое", "прочее, общее впечатление");

        public final String title;
        public final String anchorText;

        AnchorCategory(String title, String anchorText) {
            this.title = title;
            this.anchorText = anchorText;
        }
    }
    public static double cosine(double[] a, double[] b) {
        double dot = 0, na = 0, nb = 0;
        int n = Math.min(a.length, b.length);
        for (int i = 0; i < n; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }

    private static final Set<String> GENERIC = Set.of(
            "хороший","плохой","удобный","неудобный","классный","ужасный",
            "отличный","нормальный","быстрый","медленный","дорогой","дешевый",
            "лучший","худший","супер","топ","ок","неплохой"
    );

    private static final String DEFAULT_ASPECT = "Общее впечатление";

    public static List<InsightDTO> toInsights(List<String> reviewIds,
                                              List<String> texts,
                                              List<OnlineClustering.Cluster> clusters,
                                              Sentiment sentiment,
                                              int maxPerCategory,
                                              int quoteMaxLen) {

        if (reviewIds == null || texts == null || clusters == null) return List.of();
        int n = Math.min(reviewIds.size(), texts.size());
        if (n == 0 || maxPerCategory <= 0) return List.of();


        List<OnlineClustering.Cluster> sorted = new ArrayList<>(clusters);
        sorted.sort((a, b) -> Integer.compare(sizeOf(b), sizeOf(a)));


        List<InsightDTO> result = new ArrayList<>(Math.min(maxPerCategory, clusters.size()));

        for (OnlineClustering.Cluster cl : clusters) {
            if (result.size() >= maxPerCategory) break;

            List<Integer> idxs = safeIndices(cl);
            if (idxs.isEmpty()) continue;

            // Фильтруем индексы по валидному диапазону
            idxs = idxs.stream()
                    .filter(Objects::nonNull)
                    .filter(i -> i >= 0 && i < n)
                    .distinct()
                    .toList();

            if (idxs.isEmpty()) continue;

            AspectCandidate cand = buildAspectCandidate(texts, idxs);
            String aspect = cand.label();
            if (!isValidAspect(aspect)) continue;

            String statement = buildStatement(aspect, sentiment);

            int pick = pickBestEvidenceIndex(texts, idxs, cand.tokens());
            String quote = clip(texts.get(pick), quoteMaxLen);
            EvidenceDTO ev = new EvidenceDTO(reviewIds.get(pick), quote);

            result.add(new InsightDTO(
                    aspect,
                    statement,
                    sentiment,
                    idxs.size(),
                    List.of(ev)
            ));
        }

        // Результат уже ограничен maxPerCategory; доп. limit не нужен
        return result.stream()
                .sorted(Comparator.comparingInt(InsightDTO::count).reversed())
                .toList();
    }

    private static int sizeOf(OnlineClustering.Cluster cl) {
        if (cl == null || cl.indices == null) return 0;
        return cl.indices.size();
    }

    private static List<Integer> safeIndices(OnlineClustering.Cluster cl) {
        if (cl == null || cl.indices == null) return List.of();
        return cl.indices;
    }

    private static boolean isValidAspect(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty()) return false;

        String low = s.toLowerCase(Locale.ROOT);

        if (low.length() < 3) return false;
        if (low.matches("[\\p{Punct}\\d\\s]+")) return false;
        if (Set.of("censored", "unknown", "null").contains(low)) return false;

        // если аспект = одно слово и оно слишком общее — выкидываем
        if (!low.contains("/") && (STOP.contains(low) || GENERIC.contains(low))) return false;

        return true;
    }

    private static record AspectCandidate(String label, List<String> tokens) {}

    private static AspectCandidate buildAspectCandidate(List<String> texts, List<Integer> idxs) {
        Map<String, Integer> freq = new HashMap<>();

        for (int idx : idxs) {
            for (String token : tokenize(texts.get(idx))) {
                if (token.length() < 4) continue;
                if (STOP.contains(token)) continue;
                if (GENERIC.contains(token)) continue;
                if (looksLikeMostlyNumber(token)) continue;

                freq.merge(token, 1, Integer::sum);
            }
        }

        List<String> top = freq.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(2)
                .toList();

        if (top.isEmpty()) return new AspectCandidate(DEFAULT_ASPECT, List.of());

        String label = (top.size() == 1)
                ? capitalize(top.get(0))
                : capitalize(top.get(0)) + " / " + top.get(1);

        return new AspectCandidate(label, top);
    }

    private static int pickBestEvidenceIndex(List<String> texts, List<Integer> idxs, List<String> aspectTokens) {
        // Выбираем цитату, которая лучше “покрывает” токены аспекта и не слишком длинная
        int bestIdx = idxs.get(0);
        int bestScore = Integer.MIN_VALUE;
        int bestLen = Integer.MAX_VALUE;

        for (int idx : idxs) {
            String t = texts.get(idx);
            if (t == null || t.isBlank()) continue;

            int score = 0;
            if (!aspectTokens.isEmpty()) {
                String low = t.toLowerCase(Locale.ROOT);
                for (String tok : aspectTokens) {
                    if (tok != null && !tok.isBlank() && low.contains(tok)) score += 10;
                }
            }

            // Легкий бонус за “умеренную” длину (не пусто и не простыня)
            int len = normalizedLen(t);
            if (len >= 40 && len <= 240) score += 3;

            // Tie-breaker: короче лучше (для цитаты)
            if (score > bestScore || (score == bestScore && len < bestLen)) {
                bestScore = score;
                bestLen = len;
                bestIdx = idx;
            }
        }

        return bestIdx;
    }

    private static int normalizedLen(String s) {
        if (s == null) return 0;
        return s.replaceAll("\\s+", " ").trim().length();
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
        String norm = s.toLowerCase(Locale.ROOT)
                // дефис оставляем, но режем остальное
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\s-]", " ");

        String[] parts = norm.split("\\s+");
        if (parts.length == 0) return List.of();

        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) {
            if (p.isBlank()) continue;
            // уберём крайние дефисы: "--камера--" -> "камера"
            String cleaned = p.replaceAll("^-+|-+$", "");
            if (cleaned.isBlank()) continue;
            out.add(cleaned);
        }
        return out;
    }

    private static boolean looksLikeMostlyNumber(String token) {
        // чистые числа или “почти числа”
        // 2024, 12, 3-5, 128gb, s23, mi11 — обычно шум в аспектах (если нужно — ослабь правило)
        if (token.matches("\\d+")) return true;
        if (token.matches("\\d+[a-zа-я]+\\d*")) return true;
        if (token.matches("[a-zа-я]+\\d+")) return true;
        return false;
    }

    private static String clip(String s, int max) {
        if (s == null) return "";
        if (max <= 0) return "";
        s = s.replaceAll("\\s+", " ").trim();
        if (s.length() <= max) return s;
        if (max <= 3) return s.substring(0, max);
        return s.substring(0, max - 3) + "...";
    }

    private static String capitalize(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }
}

