package com.vector_search_service.ai.client;

import com.vector_search_service.ai.dto.OrderResponse;
import com.vector_search_service.ai.dto.WashRequestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "washer-service", url = "http://localhost:8085")
public interface WasherServiceClient {

    @GetMapping("/api/washer/request")
    List<WashRequestResponse> getWashRequests(@RequestHeader("Authorization") String token);

    @PostMapping("/api/washer/accept/{orderId}")
    OrderResponse acceptWashRequest(@PathVariable Long orderId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/washer/decline/{orderId}")
    OrderResponse declineWashRequest(@PathVariable Long orderId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/washer/done/{orderId}")
    OrderResponse markAsCompleted(@PathVariable Long orderId, @RequestHeader("Authorization") String token);
}