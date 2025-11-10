package com.thederailingmafia.carwash.washerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponse {
    private Long orderId;
    private Double amount;
    private LocalDateTime createdAt;
    private String paymentId;
    private String clientSecret;
    private String paymentUrl;
}
