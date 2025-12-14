package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalBotClient {

    private final ObjectMapper objectMapper;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:llama3.2}")
    private String modelName;

    public BotResponseDTO analyze(BotRequestDTO request) {
        String reviewsContext = request.reviews().stream()
                .map(r -> "AUTHOR: " + safe(r.author()) + " | TEXT: " + safe(r.text()))
                .reduce("", (a, b) -> a + "\n" + b)
                .trim();

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
                - В evidence добавляй 1–2 короткие цитаты. reviewId: если нет id — используй author.
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
                "stream", false,
                "format", "json"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/chat",
                new HttpEntity<>(payload, headers),
                String.class
        );
        log.debug(response.getBody());
        System.out.println(response.getBody());
        System.out.println(response);
        System.out.println(response.toString());
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Ollama analysis failed with status " + response.getStatusCode());
        }

        try {
            OllamaChatResponse chatResponse = objectMapper.readValue(response.getBody(), OllamaChatResponse.class);
            if (chatResponse.message() == null || chatResponse.message().content() == null) {
                throw new RuntimeException("Empty response from Ollama");
            }
            return objectMapper.readValue(chatResponse.message().content(), BotResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Ollama analysis failed: " + e.getMessage(), e);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private record OllamaChatResponse(OllamaMessage message) {
    }

    private record OllamaMessage(String role, String content) {
    }
}
