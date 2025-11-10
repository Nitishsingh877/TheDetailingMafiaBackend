package com.thederailingmafia.carwash.washerservice.client;

import com.thederailingmafia.carwash.washerservice.config.FeignClientConfig;
import com.thederailingmafia.carwash.washerservice.dto.OrderResponse;
import com.thederailingmafia.carwash.washerservice.dto.WashRequestResponse;
import org.hibernate.query.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "booking-service",configuration = FeignClientConfig.class,url = "http://localhost:8080")
public interface OrderServiceClient {

    @GetMapping("/api/order/current")
    List<OrderResponse> getCurrentOrders();

    @GetMapping("/api/order/{id}")
    OrderResponse getOrderById(@PathVariable("id") Long id);

    @PutMapping("/api/order/{id}")
    OrderResponse updateOrder(@PathVariable("id") Long id, @RequestBody OrderResponse order);

}
