package com.vector_search_service.ai.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String name;
    private String email;
    private String userRole;
}