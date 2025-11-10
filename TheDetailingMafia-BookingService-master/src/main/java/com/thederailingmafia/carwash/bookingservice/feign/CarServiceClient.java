package com.thederailingmafia.carwash.bookingservice.feign;


import com.thederailingmafia.carwash.bookingservice.config.FeignClientConfig;
import com.thederailingmafia.carwash.bookingservice.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "car-service",configuration = FeignClientConfig.class)
public interface CarServiceClient {

    @GetMapping("/car")
    OrderResponse getCars();

    @GetMapping("/car/{id}")
    OrderResponse getCarById(@PathVariable("id") Long id);
}
