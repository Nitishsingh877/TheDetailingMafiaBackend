package com.vector_search_service.ai.service;

import com.vector_search_service.ai.client.BookingServiceClient;
import com.vector_search_service.ai.client.CarServiceClient;
import com.vector_search_service.ai.client.UserServiceClient;
import com.vector_search_service.ai.client.WasherServiceClient;
import com.vector_search_service.ai.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatbotService {

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CarServiceClient carServiceClient;

    @Autowired
    private BookingServiceClient bookingServiceClient;

    @Autowired
    private WasherServiceClient washerServiceClient;

    public UserProfileResponse validateUser(String token) {
        try {
            return userServiceClient.validateUser("Bearer " + token);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token or user not found");
        }
    }

    public List<CarResponse> getUserCars(String token) {
        return carServiceClient.getCustomerCars("Bearer " + token);
    }

    public List<OrderResponse> getUserBookings(String token) {
        return bookingServiceClient.getCustomerOrders("Bearer " + token);
    }

    public List<String> getActiveWashers(String token) {
        return userServiceClient.getActiveWashers("Bearer " + token);
    }

    public OrderResponse bookWashNow(Long carId, String token) {
        CarResponse car = carServiceClient.getCarById(carId, "Bearer " + token );
        System.out.println("Car lookup result: " + car);

        if (car == null) {
            throw new RuntimeException("❌ Car not found for ID: " + carId);
        }

        BookingRequest request = new BookingRequest();
        request.setCarId(carId);

        return bookingServiceClient.bookWashNow(request, "Bearer " + token);
    }

    public OrderResponse scheduleWash(Long carId, String washerEmail, LocalDateTime scheduledTime, String token) {
        BookingRequest request = new BookingRequest();
        request.setCarId(carId);
        request.setScheduledTime(scheduledTime);
        return bookingServiceClient.scheduleWash(request, "Bearer " + token);
    }

    public void cancelBooking(Long orderId, String token) {
        bookingServiceClient.cancelBooking(orderId, "Bearer " + token);
    }

    public OrderResponse getOrderDetails(Long orderId, String token) {
        return bookingServiceClient.getOrderById(orderId, "Bearer " + token);
    }

    // Washer-specific methods
    public List<WashRequestResponse> getWashRequests(String token) {
        return washerServiceClient.getWashRequests("Bearer " + token);
    }

    public OrderResponse acceptWashRequest(Long orderId, String token) {
        return washerServiceClient.acceptWashRequest(orderId, "Bearer " + token);
    }

    public OrderResponse declineWashRequest(Long orderId, String token) {
        return washerServiceClient.declineWashRequest(orderId, "Bearer " + token);
    }

    public OrderResponse markOrderCompleted(Long orderId, String token) {
        return washerServiceClient.markAsCompleted(orderId, "Bearer " + token);
    }
}