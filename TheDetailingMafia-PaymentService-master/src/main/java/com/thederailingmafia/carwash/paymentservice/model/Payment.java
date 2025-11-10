package com.thederailingmafia.carwash.paymentservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderId;
    private String paymentId; // Stripe PaymentIntent ID
    private Double amount;
    private String customerEmail;
    private String status;
    @Column(name = "payment_url")
    @Lob
    private String paymentUrl;
}
