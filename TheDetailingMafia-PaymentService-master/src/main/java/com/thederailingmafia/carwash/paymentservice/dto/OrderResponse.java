package com.thederailingmafia.carwash.paymentservice.dto;



import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private String customerEmail;
    private String washerEmail;
    private Long carId;
    private String status;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
}


