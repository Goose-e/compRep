package com.example.companyReputationManagement.external_api;

import com.example.companyReputationManagement.dto.review.keyWord.bot.BotRequestDTO;
import com.example.companyReputationManagement.dto.review.keyWord.bot.BotResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ExternalBotClient {

    private final ObjectMapper objectMapper;

    @Value("${vertex.project-id}")
    private String projectId;

    @Value("${vertex.location:us-central1}")
    private String location;

    @Value("${vertex.model:gemini-2.5-flash}")
    private String modelName;

    public BotResponseDTO analyze(BotRequestDTO request) throws IOException {
        // 1) Готовим текст отзывов (важно: request.reviews())
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

        Schema botResponseSchema = buildBotResponseSchema();

        try (VertexAI vertexAi = new VertexAI(projectId, location)) {
            GenerativeModel model = new GenerativeModel(modelName, vertexAi)
                    .withSystemInstruction(Content.newBuilder()
                            .addParts(Part.newBuilder().setText(systemPrompt).build())
                            .build());

            GenerationConfig config = GenerationConfig.newBuilder()
                    .setResponseMimeType("application/json")
                    .setResponseSchema(botResponseSchema)
                    .build();

            GenerateContentResponse response = model.generateContent(
                    Content.newBuilder()
                            .addParts(Part.newBuilder().setText(userPrompt).build())
                            .build()
            );

            String jsonOutput = response.getCandidates(0)
                    .getContent()
                    .getParts(0)
                    .getText();

            return objectMapper.readValue(jsonOutput, BotResponseDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Gemini/VertexAI analysis failed: " + e.getMessage(), e);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * JSON Schema под BotResponseDTO:
     * BotResponseDTO(topLikes[], topDislikes[], topRequests[])
     * InsightDTO(aspect, statement, sentiment, count, evidence[])
     * EvidenceDTO(reviewId, quote)
     */
    private static Schema buildBotResponseSchema() {
        Schema evidenceSchema = Schema.newBuilder()
                .setType(Type.OBJECT)
                .putAllProperties(mapOf(
                        "reviewId", Schema.newBuilder().setType(Type.STRING).build(),
                        "quote", Schema.newBuilder().setType(Type.STRING).build()
                ))
                .addAllRequired(List.of("reviewId", "quote"))
                .build();

        Schema sentimentSchema = Schema.newBuilder()
                .setType(Type.STRING)
                .addAllEnum(List.of("POSITIVE", "NEGATIVE", "REQUEST"))
                .build();

        Schema insightSchema = Schema.newBuilder()
                .setType(Type.OBJECT)
                .putAllProperties(mapOf(
                        "aspect", Schema.newBuilder().setType(Type.STRING).build(),
                        "statement", Schema.newBuilder().setType(Type.STRING).build(),
                        "sentiment", sentimentSchema,
                        "count", Schema.newBuilder().setType(Type.INTEGER).build(),
                        "evidence", Schema.newBuilder()
                                .setType(Type.ARRAY)
                                .setItems(evidenceSchema)
                                .build()
                ))
                .addAllRequired(List.of("aspect", "statement", "sentiment", "count", "evidence"))
                .build();

        Schema insightArraySchema = Schema.newBuilder()
                .setType(Type.ARRAY)
                .setItems(insightSchema)
                .build();

        return Schema.newBuilder()
                .setType(Type.OBJECT)
                .putAllProperties(mapOf(
                        "topLikes", insightArraySchema,
                        "topDislikes", insightArraySchema,
                        "topRequests", insightArraySchema
                ))
                .addAllRequired(List.of("topLikes", "topDislikes", "topRequests"))
                .build();
    }

    private static Map<String, Schema> mapOf(Object... kv) {
        Map<String, Schema> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], (Schema) kv[i + 1]);
        }
        return m;
    }
}
