package com.thederailingmafia.carwash.washerservice.dto;

import lombok.Data;

@Data
public class InvoiceRequest {
    private Long orderId;
    private Double amount;
}
