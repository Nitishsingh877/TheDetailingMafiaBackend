package com.vector_search_service.ai.dto;

import lombok.Data;

@Data
public class WashRequestResponse {
    private Long orderId;
    private String customerEmail;
    private Long carId;
    private String status;
    private String washerEmail;
    private String paymentStatus;
}