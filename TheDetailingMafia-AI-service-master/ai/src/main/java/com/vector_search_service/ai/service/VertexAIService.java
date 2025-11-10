package com.vector_search_service.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class VertexAIService {

    private static final String MODEL = "gemini-embedding-001";

    @Value("${spring.ai.vertex.ai.gemini.project-id}")
    private String projectId;

    @Value("${vertexai.location:us-central1}")
    private String location;

    @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
    private String credentialsPath;

    private final WebClient webClient = WebClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private GoogleCredentials credentials;
    private Instant expiryTime;

    @PostConstruct
    public void init() throws IOException {
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new RuntimeException("❌ GOOGLE_APPLICATION_CREDENTIALS is not set. Configure in env or application.properties.");
        }

        try (FileInputStream fis = new FileInputStream(credentialsPath)) {
            credentials = GoogleCredentials.fromStream(fis)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
            credentials.refreshIfExpired();
            expiryTime = Instant.now().plusSeconds(300); // 5 min token expiry
        }
    }

    private String getAccessToken() throws IOException {
        if (credentials == null || expiryTime.isBefore(Instant.now())) {
            init(); // refresh credentials
        }
        return credentials.refreshAccessToken().getTokenValue();
    }

    public List<Double> getEmbedding(String text) throws Exception {
        String url = String.format(
                "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:predict",
                location, projectId, location, MODEL
        );

        String requestBody = "{ \"instances\": [{\"task_type\":\"RETRIEVAL_DOCUMENT\",\"title\":\"doc\",\"content\":\""
                + text.replace("\"", "\\\"") + "\"}] }";

        String response = webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + getAccessToken())
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode root = objectMapper.readTree(response);
        JsonNode valuesNode = root.path("predictions").get(0).path("embeddings").path("values");

        List<Double> embedding = new ArrayList<>();
        if (valuesNode.isArray()) {
            for (JsonNode value : valuesNode) {
                embedding.add(value.asDouble());
            }
        }

        if (embedding.size() != 3072) {
            throw new RuntimeException(" Embedding dimension mismatch: expected 3072, got " + embedding.size());
        }

        return embedding;
    }
}
