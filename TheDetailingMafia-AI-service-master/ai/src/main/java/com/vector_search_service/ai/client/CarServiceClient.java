package com.vector_search_service.ai.client;

import com.vector_search_service.ai.dto.CarResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "car-service", url = "http://localhost:8083")
public interface CarServiceClient {

    @GetMapping("/api/cars/all")
    List<CarResponse> getCustomerCars(@RequestHeader("Authorization") String token);

    @GetMapping("/api/cars/{id}")
    CarResponse getCarById(@PathVariable Long id, @RequestHeader("Authorization") String token);
}