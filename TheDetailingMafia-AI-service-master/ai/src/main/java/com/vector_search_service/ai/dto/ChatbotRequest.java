package com.vector_search_service.ai.dto;

import lombok.Data;

@Data
public class ChatbotRequest {
    private String message;
    private String token;
}