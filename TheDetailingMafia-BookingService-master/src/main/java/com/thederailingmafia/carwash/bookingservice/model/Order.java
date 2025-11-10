package com.thederailingmafia.carwash.bookingservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "car_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "washer_email")
    private String washerEmail;

    @Column(name = "car_id")
    private Long carId;

    @Enumerated(EnumType.STRING) // Store enum as string
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;;

    @Column(name="note")
    private String note;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
    }


}
