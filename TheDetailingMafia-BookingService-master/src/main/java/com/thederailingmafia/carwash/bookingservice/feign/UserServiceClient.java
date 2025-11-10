package com.thederailingmafia.carwash.bookingservice.feign;

import com.thederailingmafia.carwash.bookingservice.config.ExternalServiceFallback;
import com.thederailingmafia.carwash.bookingservice.config.FeignClientConfig;
import com.thederailingmafia.carwash.bookingservice.dto.WasherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8081",fallback = ExternalServiceFallback.class)
public interface UserServiceClient {
    @GetMapping("/api/users/washers")
    WasherResponse  getWashers();
}