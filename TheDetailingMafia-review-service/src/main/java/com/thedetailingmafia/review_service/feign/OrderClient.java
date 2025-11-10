package com.thedetailingmafia.review_service.feign;



import com.thedetailingmafia.review_service.config.FeignClientConfig;
import com.thedetailingmafia.review_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", url = "http://localhost:8080/api/order",configuration = FeignClientConfig.class)
public interface OrderClient {

    @GetMapping("/{id}")
    OrderResponse getOrderById(@PathVariable("id") Long orderId);


}
