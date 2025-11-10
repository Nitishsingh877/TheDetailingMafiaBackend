package com.vector_search_service.ai.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetrievalService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private VertexAIService vertexAIService;

    public List<String> searchSimilarDocs(String query, int topK) throws Exception {
        // 1. Generate embedding for query
        List<Double> queryEmbedding = vertexAIService.getEmbedding(query);

        // Convert embedding into Postgres array format for pgvector
        String vectorLiteral = queryEmbedding.toString()
                .replace("[", "[")
                .replace("]", "]"); // pgvector expects [x,y,z]

        // 2. Query Postgres with <-> cosine distance
        String sql = """
            SELECT content
            FROM document_embeddings
            ORDER BY embedding <-> ?::vector
            LIMIT ?
        """;

        return jdbcTemplate.query(
                sql,
                new Object[]{vectorLiteral, topK},
                (rs, rowNum) -> rs.getString("content")
        );
    }
}

