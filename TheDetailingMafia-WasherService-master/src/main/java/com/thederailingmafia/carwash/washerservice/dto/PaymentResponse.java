package com.thederailingmafia.carwash.washerservice.dto;



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
    private String orderStatus;
    private String clientSecret;
    private String paymentUrl;
}
