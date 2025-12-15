package com.example.companyReputationManagement.external_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalEmbeddingClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.embedding-model:embeddinggemma:latest}")
    private String embeddingModel;

    @Value("${ollama.client.max-retries:3}")
    private int maxRetries;

    @Value("${ollama.client.retry-delay:2s}")
    private Duration retryDelay;

    public double[] embed(String text) {
        text = text == null ? "" : text;
        Map<String, Object> payload = Map.of(
                "model", embeddingModel,
                "prompt", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            return postForEmbedding(baseUrl + "/api/embeddings", entity);
        } catch (HttpClientErrorException.NotFound notFound) {
            return postForEmbedding(baseUrl + "/api/embed", entity);
        }
    }

    private double[] postForEmbedding(String url, HttpEntity<Map<String, Object>> entity) {
        int attempt = 0;
        while (true) {
            try {
                ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
                if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                    throw new RestClientException("Bad status: " + resp.getStatusCode());
                }
                EmbeddingResponse parsed = objectMapper.readValue(resp.getBody(), EmbeddingResponse.class);

                if (parsed.embedding != null && !parsed.embedding.isEmpty()) {
                    return toArray(parsed.embedding);
                }

                if (parsed.embeddings != null && !parsed.embeddings.isEmpty() && !parsed.embeddings.get(0).isEmpty()) {
                    return toArray(parsed.embeddings.get(0));
                }

                throw new RuntimeException("Empty embedding from Ollama: " + url);

            } catch (RestClientException ex) {
                attempt++;
                if (attempt >= maxRetries) {
                    throw new RuntimeException("Embedding request failed after retries: " + ex.getMessage(), ex);
                }
                log.warn("Retry embedding (attempt {}/{}): {}", attempt, maxRetries, ex.getMessage());
                sleep();
            } catch (Exception parseEx) {
                throw new RuntimeException("Failed to parse embedding response: " + parseEx.getMessage(), parseEx);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(retryDelay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static double[] toArray(List<Double> list) {
        double[] a = new double[list.size()];
        for (int i = 0; i < list.size(); i++) a[i] = list.get(i);
        return a;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class EmbeddingResponse {
        public List<Double> embedding;
        public List<List<Double>> embeddings;
    }
}
