package com.thederailingmafia.carwash.paymentservice.controller;


import com.thederailingmafia.carwash.paymentservice.dto.PaymentRequest;
import com.thederailingmafia.carwash.paymentservice.dto.PaymentResponse;
import com.thederailingmafia.carwash.paymentservice.dto.PendingPaymentDto;
import com.thederailingmafia.carwash.paymentservice.model.Payment;
import com.thederailingmafia.carwash.paymentservice.repository.PaymentRepository;
import com.thederailingmafia.carwash.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {


    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @GetMapping("/health")
    public String health() {
        return "OK";
    }


    @PostMapping("/process")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String requestorEmail = auth.getName();

          //  System.out.println("Received payment request from: " + requestorEmail);

            PaymentResponse response = paymentService.processPayment(request, requestorEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Failed to process payment: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping(value = "/confirm/{paymentId}")
    public ResponseEntity<Map<String, String>> confirmPayment(@PathVariable String paymentId) {
        try {
            PaymentResponse response = paymentService.confirmPayment(paymentId);

            // Ensure payment status is updated in the response
            Map<String, String> result = new HashMap<>();
            result.put("message", "Payment confirmed");
            result.put("paymentId", response.getPaymentId());
            result.put("status", response.getStatus()); // Include updated payment status
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Confirm error for paymentId " + paymentId + ": " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to confirm payment: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<List<PendingPaymentDto>> getPendingPayments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        try {
          //  System.out.println("aaya ji");
            List<Payment> pendingPayments = paymentRepository.findByCustomerEmailAndStatus(email, "PENDING");


            List<PendingPaymentDto> result = pendingPayments.stream()
                    .map(payment -> new PendingPaymentDto(
                            payment.getOrderId(),
                            payment.getAmount(),
                            payment.getPaymentUrl(),
                            payment.getPaymentId()
                    ))
                    .toList();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("Failed to fetch pending payments for " + email + ": " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/status/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'WASHER', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable Long orderId) {
        try {
            PaymentResponse statusResponse = paymentService.getPaymentStatus(orderId);

            if (statusResponse == null) {
                return ResponseEntity.status(404).body(null);
            }

            return ResponseEntity.ok(statusResponse);
        } catch (RuntimeException e) {
            System.err.println("Error retrieving payment status for orderId " + orderId + ": " + e.getMessage());
            return ResponseEntity.status(500).body(new PaymentResponse(null, orderId, null, "ERROR", null, null));
        }
    }

}