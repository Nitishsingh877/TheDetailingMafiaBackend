package com.thederailingmafia.carwash.bookingservice.config;

import com.thederailingmafia.carwash.bookingservice.dto.WasherResponse;
import com.thederailingmafia.carwash.bookingservice.feign.UserServiceClient;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class ExternalServiceFallback implements UserServiceClient {
    @Override
    public WasherResponse getWashers() {
        System.err.println("Fallback triggered: User service unavailable.");
//        return Collections.singletonList("Booking confirmed. Washer assignment pending due to high demand.");
        return new WasherResponse(Collections.emptyList(),true);
    }
}