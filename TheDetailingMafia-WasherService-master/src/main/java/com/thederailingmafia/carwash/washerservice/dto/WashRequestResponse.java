package com.thederailingmafia.carwash.washerservice.dto;

import lombok.Data;

@Data
public class WashRequestResponse {
    private Long orderId;
    private String customerEmail;
    private Long carId;
    private String status;
    private String washerEmail;
    private String paymentStatus;
//    private String washerEmail;
}
