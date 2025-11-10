package com.thederailingmafia.carwash.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;     
    private Long orderId;
    private Double amount;
    private String status;
    private String successUrl;
    private String paymentUrl;
}
