package com.thederailingmafia.carwash.washerservice.client;

import com.thederailingmafia.carwash.washerservice.dto.PaymentRequest;
import com.thederailingmafia.carwash.washerservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "http://localhost:8086")
public interface PaymentServiceClient {
    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestBody PaymentRequest request
    );

    @GetMapping("/api/payments/status/{orderId}")
    PaymentResponse getPaymentStatus(@PathVariable Long orderId);



}
