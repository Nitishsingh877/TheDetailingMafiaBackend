package com.thederailingmafia.carwash.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingPaymentDto {
        private Long orderId;
        private Double amount;
        private String paymentUrl;
        private String paymentId;


    }


