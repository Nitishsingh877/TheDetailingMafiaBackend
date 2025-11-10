package com.thederailingmafia.carwash.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thederailingmafia.carwash.bookingservice.config.RabbitConfig;
import com.thederailingmafia.carwash.bookingservice.dto.OrderRequest;
import com.thederailingmafia.carwash.bookingservice.dto.OrderResponse;
import com.thederailingmafia.carwash.bookingservice.dto.WasherResponse;
import com.thederailingmafia.carwash.bookingservice.exception.InvalidRoleException;
import com.thederailingmafia.carwash.bookingservice.exception.OrderNotFoundException;
import com.thederailingmafia.carwash.bookingservice.feign.UserServiceClient;
import com.thederailingmafia.carwash.bookingservice.model.Order;
import com.thederailingmafia.carwash.bookingservice.model.OrderStatus;
import com.thederailingmafia.carwash.bookingservice.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserServiceClient userServiceClient;

    private final ObjectMapper objectMapper = new ObjectMapper();
//    The ObjectMapper class in Jackson is used for serializing Java objects to JSON and deserializing JSON to Java objects.


    public WasherResponse getListOfWasher() {
        return userServiceClient.getWashers();
    }

    public WasherResponse getValidWasherResponse() {
        WasherResponse response = getListOfWasher();
        System.out.println(response);

        List<String> filteredWashers = response.getWashers();
        System.out.println("filterd washer " + filteredWashers);

        return new WasherResponse(filteredWashers, response.isFallbackTriggered());
    }

    public OrderResponse bookWashNow(OrderRequest request, String userEmail) {

        if (request == null) {
            throw new RuntimeException("Booking request cannot be null");
        }
        if (request.getCarId() == null || request.getCarId() <= 0) {
            throw new RuntimeException("Valid car ID is required for booking");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("Customer email is required for booking");
        }
        try {
            Order order = new Order();
            order.setCustomerEmail(userEmail.trim());
            order.setCarId(request.getCarId());
            order.setScheduledTime(java.time.LocalDateTime.now().plusHours(1)); // Default 1 hour from now

            Order savedOrder = orderRepository.save(order);
            publishOrderEvent(savedOrder, "order.created");
            return mapToResponse(savedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create booking: " + e.getMessage());
        }
    }

    public OrderResponse scheduleWashNow(OrderRequest request, String userEmail) {
        if (request == null) {
            throw new RuntimeException("Schedule booking request cannot be null");
        }
        if (request.getCarId() == null || request.getCarId() <= 0) {
            throw new RuntimeException("Valid car ID is required for scheduled booking");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("Customer email is required for scheduled booking");
        }
        if (request.getScheduledTime() == null) {
            throw new RuntimeException("Scheduled time is required for scheduled booking");
        }
        if (request.getScheduledTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Scheduled time must be in the future");
        }

        try {
            Order order = new Order();
            order.setCustomerEmail(userEmail.trim());
            order.setCarId(request.getCarId());
            order.setScheduledTime(request.getScheduledTime());
            Order savedOrder = orderRepository.save(order);
            publishOrderEvent(savedOrder, "order.created");
            return mapToResponse(savedOrder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to schedule booking: " + e.getMessage());
        }
    }

    public List<OrderResponse> getPendingOrders() {
        return orderRepository.findByStatus(OrderStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    //    public OrderResponse assignOrder(Long orderId, String washerEmail) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
//
//        if(order.getStatus() != OrderStatus.PENDING){
//            throw new OrderNotFoundException("Order status is not PENDING");
//        }
//
//        order.setWasherEmail(washerEmail);
//        order.setStatus(OrderStatus.ASSIGNED);
//        Order savedOrder = orderRepository.save(order);
//        publishOrderEvent(savedOrder, "order.assigned");
//        return  mapToResponse(savedOrder);
//    }
    //New: Automatic scheduling
    @Scheduled(fixedRate = 30000)
    public void assignOrder() {
//        early exit , for minimal use of resource
        long count = orderRepository.countByStatus(OrderStatus.PENDING);
        if (count == 0) {
            return;
        }

        try {
            List<Order> unassignedOrders = orderRepository.findByStatus(OrderStatus.PENDING);

            WasherResponse washerResponse = getValidWasherResponse();
            List<String> washers = washerResponse.getWashers();

            if (washers.isEmpty()) {
                for (Order order : unassignedOrders) {
                    order.setNote("Washer assignment delayed due to temporary service outage. We'll get your sparkle squad back online soon!");
                    orderRepository.save(order);
                }
                if (washerResponse.isFallbackTriggered()) {
                    System.err.println("Fallback triggered: Washer assignment deferred.");
                }
                return;
            }

            Random random = new Random();
            for (Order order : unassignedOrders) {
                String washerEmail = washers.get(random.nextInt(washers.size()));
                order.setWasherEmail(washerEmail);
                order.setStatus(OrderStatus.ASSIGNED);
                order.setNote("All set! Your washer’s on the way—time to let your ride shine.");
                orderRepository.save(order);

                System.out.println("Assigned order " + order.getId() + " to washer " + washerEmail);
                publishOrderEvent(order, "order.assigned");
            }
        } catch (Exception e) {
            System.err.println("Error in assignOrders: " + e.getMessage());
        }
    }

    public List<OrderResponse> getCurrentOrders(String userEmail, String role) {
        List<Order> orders;
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        // System.out.println("OrderService getCurrentOrders for " + userEmail + " (" + normalizedRole + ")");
        if ("CUSTOMER".equals(normalizedRole)) {
            orders = orderRepository.findByCustomerEmail(userEmail);

        } else if ("WASHER".equals(normalizedRole)) {
            orders = orderRepository.findByWasherEmail(userEmail);
        } else if ("ADMIN".equals(normalizedRole)) {
            orders = orderRepository.findByWasherEmail(userEmail);
        } else {
            throw new RuntimeException("Invalid role: " + normalizedRole);

        }

//        orders.forEach(System.out::println);

        return orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.ASSIGNED || o.getStatus() == OrderStatus.ACCEPTED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getPastOrders(String userEmail, String role) {
        List<Order> orders;

        try {
            if ("ROLE_CUSTOMER".equals(role)) {
                orders = orderRepository.findByCustomerEmail(userEmail);
            } else if ("ROLE_WASHER".equals(role)) {
                orders = orderRepository.findByWasherEmail(userEmail);
            } else {
                throw new InvalidRoleException("Invalid role");
            }

            if (orders == null) {
                System.out.println("No orders found for user: " + userEmail);
                return Collections.emptyList();
            }

            return orders.stream()
                    .filter(o -> o != null && o.getStatus() != null &&
                            (o.getStatus() == OrderStatus.COMPLETED))
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            System.err.println("Error in getPastOrders(): " + ex.getMessage());
            ex.printStackTrace();
            throw ex; // will trigger 500 if unhandled
        }
    }


    public OrderResponse getOrder(Long orderId, String userEmail, String role) {
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getCustomerEmail().equals(userEmail) ||
                        (o.getWasherEmail() != null && o.getWasherEmail().equals(userEmail)) ||
                        "ADMIN".equals(normalizedRole))
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return  mapToResponse(order);
    }


    // OrderService.java
    public OrderResponse updateOrder(Long id, OrderResponse request, String userEmail, String role) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        //System.out.println("OrderService updating order " + id + " by " + userEmail + " (" + role + ")");
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;

        // Check permissions based on role
        if ("WASHER".equals(normalizedRole)) {
            if (!userEmail.equals(order.getWasherEmail())) {
                throw new RuntimeException("Order not assigned to this washer");
            }
        } else if ("CUSTOMER".equals(normalizedRole)) {
            if (!userEmail.equals(order.getCustomerEmail())) {
                throw new RuntimeException("Order not owned by this customer");
            }
        } else if (!"ADMIN".equals(normalizedRole)) {
            throw new RuntimeException("Unauthorized role: " + normalizedRole);
        }

        String statusStr = request.getStatus();
        if (statusStr == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.trim().toUpperCase());
            order.setStatus(status);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status value: " + statusStr + ", error: " + e.getMessage());
            throw new RuntimeException("Invalid status: " + statusStr);
        }

        if (request.getWasherEmail() != null) {
            order.setWasherEmail(request.getWasherEmail());
        }
        Order updatedOrder = orderRepository.save(order);
        publishOrderEvent(updatedOrder, "order.updated");
        //  System.out.println("OrderService updated order: " + updatedOrder.getId() + ", status: " + updatedOrder.getStatus());
        return mapToResponse(updatedOrder);
    }
    private void publishOrderEvent(Order order, String eventType) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("event", eventType);
            event.put("orderId", order.getId());
            event.put("customerEmail", order.getCustomerEmail());
            event.put("washerEmail", order.getWasherEmail());
            event.put("status", order.getStatus().name());
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, "notification." + eventType, objectMapper.writeValueAsString(event));
            System.out.println("Published event: " + eventType + " for order " + order.getId());
        } catch (Exception e) {
            System.err.println("Error publishing event " + eventType + ": " + e.getMessage());
        }
    }


    // Enhanced cancel order method with comprehensive validation

    public OrderResponse cancelOrder(Long orderId, String userEmail, String role) {

        if (orderId == null || orderId <= 0) {
            throw new RuntimeException("Invalid order ID for cancellation");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email is required for cancellation");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new RuntimeException("User role is required for cancellation");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        String normalizedRole = role.startsWith("ROLE_") ? role.substring(5) : role;
        if ("CUSTOMER".equals(normalizedRole)) {
            if (!userEmail.trim().equals(order.getCustomerEmail())) {
                throw new RuntimeException("Customers can only cancel their own orders");
            }
        } else if (!"ADMIN".equals(normalizedRole)) {
            throw new RuntimeException("Only customers and admins can cancel orders");
        }


        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed orders");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new RuntimeException("Order is already cancelled");
        }
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.ASSIGNED) {
            throw new RuntimeException("Order cannot be cancelled in current status: " + order.getStatus());
        }

        try {
            order.setStatus(OrderStatus.CANCELED);
            Order cancelledOrder = orderRepository.save(order);
            publishOrderEvent(cancelledOrder, "order.cancelled");
            return mapToResponse(cancelledOrder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to cancel order: " + e.getMessage());
        }
    }


    public Boolean orderExists(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    /**
     * Dashboard analytics methods for booking statistics
     */
    public long getCustomerBookingsCount(String customerEmail) {
        return orderRepository.countByCustomerEmail(customerEmail);
    }

    public long getCustomerPendingCount(String customerEmail) {
        return orderRepository.findByCustomerEmail(customerEmail).stream()
                .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.ASSIGNED)
                .count();
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setWasherEmail(order.getWasherEmail());
        response.setCarId(order.getCarId());
        response.setStatus(order.getStatus().name());
        response.setScheduledTime(order.getScheduledTime());
        response.setCreatedAt(order.getCreatedAt());
        response.setNote(order.getNote());
        return response;
    }
}
