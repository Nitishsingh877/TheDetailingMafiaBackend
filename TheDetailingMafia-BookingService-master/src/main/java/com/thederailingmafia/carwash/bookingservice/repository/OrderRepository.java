package com.thederailingmafia.carwash.bookingservice.repository;

import com.thederailingmafia.carwash.bookingservice.model.Order;
import com.thederailingmafia.carwash.bookingservice.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerEmail(String email);
    List<Order> findByWasherEmail(String email);
    List<Order> findByStatus(String status);

    long countByStatus(OrderStatus orderStatus);

    /**
     * Dashboard analytics repository methods
     */
    long countByCustomerEmail(String customerEmail);
}
