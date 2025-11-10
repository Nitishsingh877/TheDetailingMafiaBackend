package com.thederailingmafia.carwash.bookingservice.dto;

import com.thederailingmafia.carwash.bookingservice.model.OrderStatus;
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
    private String note;
}
