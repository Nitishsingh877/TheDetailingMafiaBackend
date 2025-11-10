package com.vector_search_service.ai.client;

import com.vector_search_service.ai.dto.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {

    @GetMapping("/api/users/profile")
    UserProfileResponse getUserProfile(@RequestHeader("Authorization") String token);

    @GetMapping("/api/users/washers")
    List<String> getActiveWashers(@RequestHeader("Authorization") String token);

    @GetMapping("/api/users/validate")
    UserProfileResponse validateUser(@RequestHeader("Authorization") String token);
}