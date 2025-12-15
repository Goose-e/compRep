package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.*;
import com.example.companyReputationManagement.models.enums.Sentiment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalBotClient {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:gpt-oss:latest}")
    private String modelName;

    @Value("${ollama.client.max-retries:3}")
    private int maxRetries;

    @Value("${ollama.client.retry-delay:2s}")
    private Duration retryDelay;


    public BotResponseDTO analyze(BotRequestDTO request) {
        String reviewsContext = buildReviewsContext(request);

        String systemPrompt = """
            Ты — эксперт-аналитик отзывов на русском.

            Верни ТОЛЬКО валидный JSON. Никакого markdown, текста или комментариев.

            Формат СТРОГО:
            {
              "topLikes": InsightDTO[],
              "topDislikes": InsightDTO[],
              "topRequests": InsightDTO[]
            }

            InsightDTO:
            {
              "aspect": string,
              "statement": string,
              "sentiment": "POSITIVE" | "NEGATIVE" | "REQUEST",
              "count": number,
              "evidence": [ { "reviewId": string, "quote": string } ]
            }

            Ограничения:
            - максимум 5 элементов в каждом массиве
            - evidence: 1 цитата
            - reviewId вида review-1, review-2
            - если данных нет — верни пустые массивы
            """;

        String userPrompt = "Отзывы:\n\n" + reviewsContext;

        Map<String, Object> payload = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "stream", false,
                "options", Map.of(
                        "num_ctx", 1024,
                        "num_predict", 800,
                        "temperature", 0.1,
                        "seed", 42
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = postWithRetry(new HttpEntity<>(payload, headers));
        String body = response.getBody();

        if (body == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Ollama failed: " + response.getStatusCode());
        }

        try {
            JsonNode root = objectMapper.readTree(body);
            OllamaChatResponse chat = objectMapper.treeToValue(root, OllamaChatResponse.class);

            if (chat.message() == null || chat.message().content() == null) {
                throw new RuntimeException("Empty Ollama message");
            }

            String json = extractJson(chat.message().content());
            if (!isCompleteJson(json)) {
                throw new RuntimeException("Incomplete JSON in Ollama response");
            }
            JsonNode normalized = normalizeBotJson(objectMapper.readTree(json));

            return objectMapper.treeToValue(normalized, BotResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Ollama analysis failed: " + e.getMessage(), e);
        }
    }



    private JsonNode normalizeBotJson(JsonNode root) throws JsonProcessingException {
        if (!(root instanceof ObjectNode obj)) return root;

        normalizeInsightArray(obj, "topLikes", Sentiment.POSITIVE);
        normalizeInsightArray(obj, "topDislikes", Sentiment.NEGATIVE);
        normalizeInsightArray(obj, "topRequests", Sentiment.REQUEST);

        return obj;
    }

    private void normalizeInsightArray(ObjectNode obj, String field, Sentiment fallback) throws JsonProcessingException {
        ArrayNode out = objectMapper.createArrayNode();
        JsonNode raw = obj.get(field);

        if (raw != null && raw.isArray()) {
            for (JsonNode el : raw) {
                JsonNode node = el.isTextual()
                        ? objectMapper.readTree(el.asText())
                        : el;

                ObjectNode insight = (ObjectNode) node;

                insight.putIfAbsent(
                        "sentiment",
                        JsonNodeFactory.instance.textNode(fallback.name())
                );
                insight.putIfAbsent("count", JsonNodeFactory.instance.numberNode(1));

                if (!insight.has("evidence") || !insight.get("evidence").isArray()) {
                    insight.set("evidence", objectMapper.createArrayNode());
                }

                out.add(insight);
            }
        }
        obj.set(field, out);
    }



    private ResponseEntity<String> postWithRetry(HttpEntity<?> entity) {
        int attempt = 0;
        while (true) {
            try {
                return restTemplate.postForEntity(baseUrl + "/api/chat", entity, String.class);
            } catch (RestClientException ex) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Failed after retries", ex);
                }
                log.warn("Retry {}/{}: {}", attempt, maxRetries, ex.getMessage());
                sleep();
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(retryDelay.toMillis());
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private static String extractJson(String text) {
        int s = text.indexOf('{');
        int e = text.lastIndexOf('}');
        return (s >= 0 && e > s) ? text.substring(s, e + 1) : text;
    }

    private static boolean isCompleteJson(String json) {
        if (json == null) return false;

        boolean inString = false;
        boolean escaped = false;
        int depth = 0;

        for (char ch : json.toCharArray()) {
            if (escaped) {
                escaped = false;
                continue;
            }

            if (ch == '\\') {
                escaped = inString;
                continue;
            }

            if (ch == '"') {
                inString = !inString;
                continue;
            }

            if (inString) continue;

            if (ch == '{' || ch == '[') depth++;
            else if (ch == '}' || ch == ']') depth--;
        }

        return depth == 0 && !inString && json.trim().startsWith("{") && json.trim().endsWith("}");
    }

    private String buildReviewsContext(BotRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < request.reviews().size(); i++) {
            sb.append("review-").append(i + 1).append(": ")
                    .append(safe(request.reviews().get(i).text()))
                    .append("\n");
        }
        return sb.toString().trim();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /* ===================== OLLAMA DTO ===================== */

    private record OllamaChatResponse(OllamaMessage message) {}
    private record OllamaMessage(String role, String content) {}
}
