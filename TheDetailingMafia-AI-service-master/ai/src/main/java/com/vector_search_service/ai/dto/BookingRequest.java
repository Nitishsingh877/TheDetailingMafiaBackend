package com.vector_search_service.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long carId;
    private LocalDateTime scheduledTime;
}