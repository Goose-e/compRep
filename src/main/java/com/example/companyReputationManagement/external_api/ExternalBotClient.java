package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotReviewDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    public BotResponseDTO analyze(BotRequestDTO request) {
        String reviewsContext = buildReviewsContext(request);

        String systemPrompt =
                """
                Ты — эксперт-аналитик отзывов на русском.

                Верни ТОЛЬКО валидный JSON (без markdown, без пояснений, без текста вокруг).

                СТРОГОЕ ТРЕБОВАНИЕ К ФОРМАТУ:
                - topLikes, topDislikes, topRequests — это МАССИВЫ ОБЪЕКТОВ (НЕ массивы строк).
                - Каждый элемент массива — объект InsightDTO со СТРОГО следующими полями:
                  {
                    "aspect": string,
                    "statement": string,
                    "sentiment": "POSITIVE" | "NEGATIVE" | "REQUEST",
                    "count": integer >= 1,
                    "evidence": [ { "reviewId": string, "quote": string } ]
                  }
                - Никаких дополнительных полей.
                - В evidence добавляй 1–2 короткие цитаты. reviewId используй вида "review-<номер отзыва>".
                - Если данных мало — верни пустые массивы, но поля topLikes/topDislikes/topRequests должны присутствовать всегда.

                Верни результат в виде одного JSON-объекта.
                """;

        String userPrompt =
                "Проанализируй отзывы и верни JSON:\n\n" + reviewsContext;

        Map<String, Object> payload = Map.of(
                "model", modelName,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "options", Map.of(
                        "num_ctx", 1024,
                        "num_predict", 400,
                        "temperature", 0.1,
                        "top_p", 0.9,
                        "repeat_penalty", 1.1,
                        "seed", 42
                ),
                "stream", false
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = postWithRetry(new HttpEntity<>(payload, headers));
        String body = response.getBody();
        String debugBody = body == null ? "" : body.substring(0, Math.min(body.length(), 2000));
        log.debug("Ollama status={}, bodySize={}, bodySample={}", response.getStatusCode(),
                body == null ? 0 : body.length(), debugBody);

        if (body == null || !response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Ollama analysis failed with status " + response.getStatusCode());
        }

        try {
            JsonNode rootNode = objectMapper.readTree(body);
            if (rootNode.hasNonNull("error")) {
                throw new RuntimeException("Ollama error: " + rootNode.get("error").asText());
            }

            OllamaChatResponse chatResponse = objectMapper.treeToValue(rootNode, OllamaChatResponse.class);
            if (chatResponse.message() == null || chatResponse.message().content() == null) {
                throw new RuntimeException("Empty response from Ollama");
            }

            String extractedJson = extractJsonObject(chatResponse.message().content());
            JsonNode normalized = normalizeBotResponseJson(objectMapper.readTree(extractedJson));
            return objectMapper.treeToValue(normalized, BotResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Ollama analysis failed: " + e.getMessage(), e);
        }
    }

    private ResponseEntity<String> postWithRetry(HttpEntity<Map<String, Object>> entity) {
        int attempts = 0;
        while (true) {
            attempts++;
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                        baseUrl + "/api/chat",
                        entity,
                        String.class
                );

                if (response.getStatusCode().is5xxServerError()) {
                    throw new RestClientException("Server error: " + response.getStatusCode());
                }

                return response;
            } catch (RestClientException ex) {
                if (attempts >= 2) {
                    throw new RuntimeException("Failed to reach Ollama after retry: " + ex.getMessage(), ex);
                }
                log.warn("Retrying Ollama request after failure: {}", ex.getMessage());
            }
        }
    }

    private String extractJsonObject(String raw) {
        int start = raw.indexOf('{');
        int end = raw.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return raw.substring(start, end + 1);
        }
        return raw;
    }

    private JsonNode normalizeBotResponseJson(JsonNode node) throws JsonProcessingException {
        if (node.isTextual()) {
            return normalizeBotResponseJson(objectMapper.readTree(node.asText()));
        }

        if (!(node instanceof ObjectNode objectNode)) {
            return node;
        }

        List<String> fields = List.of("topLikes", "topDislikes", "topRequests");
        for (String field : fields) {
            JsonNode arrayNode = objectNode.get(field);
            if (arrayNode == null || arrayNode.isNull()) {
                objectNode.set(field, objectMapper.createArrayNode());
                continue;
            }

            if (arrayNode.isArray()) {
                ArrayNode normalizedArray = objectMapper.createArrayNode();
                for (JsonNode element : arrayNode) {
                    JsonNode normalizedElement = element.isTextual()
                            ? normalizeBotResponseJson(objectMapper.readTree(element.asText()))
                            : normalizeBotResponseJson(element);
                    normalizedArray.add(normalizedElement);
                }
                objectNode.set(field, normalizedArray);
            }
        }

        return objectNode;
    }

    private String buildReviewsContext(BotRequestDTO request) {
        StringBuilder builder = new StringBuilder();
        List<BotReviewDTO> reviews = request.reviews();
        for (int i = 0; i < reviews.size(); i++) {
            builder.append("review-").append(i + 1).append(": ")
                    .append(safe(reviews.get(i).text()))
                    .append("\n");
        }
        return builder.toString().trim();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private record OllamaChatResponse(OllamaMessage message) {
    }

    private record OllamaMessage(String role, String content) {
    }
}
