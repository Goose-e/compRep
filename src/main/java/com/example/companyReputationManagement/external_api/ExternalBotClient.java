package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.models.enums.Sentiment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
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
                
                Верни ТОЛЬКО валидный JSON.
                Ответ ДОЛЖЕН начинаться с символа { и заканчиваться символом }.
                Никакого markdown, текста или комментариев.
                НЕ рассуждай. НЕ используй thinking. Сразу верни JSON.
                
                
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
                - максимум 3 элемента в каждом массиве
                - evidence: РОВНО 1 цитата
                - quote не длиннее 120 символов
                - reviewId вида review-1, review-2
                - если есть хотя бы 1 отзыв, попытайся вернуть хотя бы 1 Insight
                - если данных действительно нет — верни пустые массивы
                """;


        String userPrompt = "Отзывы:\n\n" + reviewsContext;
        log.info("model={}, promptLen={}", modelName, userPrompt.length());
        log.info("userPromptHead={}", userPrompt.substring(0, Math.min(200, userPrompt.length())));

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
            String content = extractBotContent(body);

            if (content == null || content.isBlank()) {
                throw new RuntimeException("Empty Ollama message");
            }

            String json = extractJson(content);
            JsonNode normalized = normalizeBotJson(ensureCompleteJson(json));

            return objectMapper.treeToValue(normalized, BotResponseDTO.class);

        } catch (Exception e) {
            throw new RuntimeException("Ollama analysis failed: " + e.getMessage() + " " + response, e);
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

    private String extractBotContent(String body) throws JsonProcessingException {
        if (body == null) return "";

        try {
            JsonNode root = objectMapper.readTree(body);

            if (root.isTextual()) {
                return root.asText();
            }

            JsonNode messageNode = root.get("message");
            if (messageNode != null) {
                JsonNode contentNode = messageNode.get("content");
                if (contentNode != null && contentNode.isTextual()) {
                    return contentNode.asText();
                }
            }

            JsonNode contentNode = root.get("content");
            if (contentNode != null && contentNode.isTextual()) {
                return contentNode.asText();
            }

            return body;
        } catch (JsonProcessingException e) {
            log.warn("Bot response is not valid JSON, using raw body: {}", e.getOriginalMessage());
            return body;
        }
    }

    private JsonNode ensureCompleteJson(String json) throws JsonProcessingException {
        if (isCompleteJson(json)) {
            return objectMapper.readTree(json);
        }

        String fixedJson = attemptJsonRepair(json);
        if (fixedJson != null && isCompleteJson(fixedJson)) {
            log.warn("Incomplete JSON detected; auto-repaired Ollama response");
            return objectMapper.readTree(fixedJson);
        }

        throw new RuntimeException("Incomplete JSON in Ollama response: unable to auto-repair");
    }

    private String attemptJsonRepair(String json) {
        if (json == null) return null;

        String trimmed = json.trim();
        if (trimmed.isEmpty()) return null;

        StringBuilder sb = new StringBuilder(trimmed);
        Deque<Character> stack = new ArrayDeque<>();
        boolean inString = false;
        boolean escaped = false;

        for (char ch : trimmed.toCharArray()) {
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

            if (ch == '{' || ch == '[') {
                stack.push(ch == '{' ? '}' : ']');
            } else if (ch == '}' || ch == ']') {
                if (stack.isEmpty()) {
                    return null;
                }
                char expected = stack.pop();
                if ((ch == '}' && expected != '}') || (ch == ']' && expected != ']')) {
                    return null;
                }
            }
        }

        if (inString) return null;

        while (!stack.isEmpty()) {
            sb.append(stack.pop());
        }

        return sb.toString();
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

}
