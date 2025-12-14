package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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
                "Ты — эксперт-аналитик отзывов на русском. " +
                        "Верни СТРОГО JSON по заданной схеме. " +
                        "Сгруппируй ключевые моменты в topLikes, topDislikes, topRequests. " +
                        "В evidence добавляй короткие цитаты и идентификатор (если нет id — используй author).";

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
