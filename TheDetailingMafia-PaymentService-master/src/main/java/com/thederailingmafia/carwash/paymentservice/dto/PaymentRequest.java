package com.thederailingmafia.carwash.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private Double amount; // Invoice amount
    private Double invoiceAmount; // From WasherService invoice
}
