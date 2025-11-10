package com.vector_search_service.ai.client;

import com.vector_search_service.ai.dto.BookingRequest;
import com.vector_search_service.ai.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "booking-service", url = "http://localhost:8084")
public interface BookingServiceClient {

    @GetMapping("/api/order/past")
    List<OrderResponse> getCustomerOrders(@RequestHeader("Authorization") String token);

    @PostMapping("/api/order/wash-now")
    OrderResponse bookWashNow(@RequestBody BookingRequest request, @RequestHeader("Authorization") String token);

//    not working as of now
    @PostMapping("/api/orders/schedule")
    OrderResponse scheduleWash(@RequestBody BookingRequest request, @RequestHeader("Authorization") String token);

    @DeleteMapping("/api/order/{id}/cancel")
    void cancelBooking(@PathVariable Long id, @RequestHeader("Authorization") String token);

    @GetMapping("/api/order/{id}")
    OrderResponse getOrderById(@PathVariable Long id, @RequestHeader("Authorization") String token);
}