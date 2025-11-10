package com.thederailingmafia.carwash.washerservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thederailingmafia.carwash.washerservice.client.OrderServiceClient;
import com.thederailingmafia.carwash.washerservice.client.PaymentServiceClient;
import com.thederailingmafia.carwash.washerservice.dto.*;
import com.thederailingmafia.carwash.washerservice.model.Invoice;
import com.thederailingmafia.carwash.washerservice.repository.InvoiceRepository;
import feign.FeignException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WasherService {

    @Autowired
    private OrderServiceClient orderServiceClient;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();


    public List<WashRequestResponse> getWashRequest(String washerEmail) {
        try {
           // System.out.println(" Fetching orders for washer: " + washerEmail);
            List<OrderResponse> orders = orderServiceClient.getCurrentOrders();
           // System.out.println("Total orders fetched: " + orders.size());

            List<WashRequestResponse> washerOrders = orders.stream()
                    .filter(o -> {
                        boolean matches = washerEmail.equals(o.getWasherEmail());
                        if (matches) {
                            System.out.println("✅ Order " + o.getId() + " matches washer " + washerEmail);
                        }
                        return matches;
                    })
                    .filter(o -> {
                        String status = o.getStatus();
                        boolean validStatus = "ASSIGNED".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status);
                       // System.out.println(" Order " + o.getId() + " status: " + status + " (valid: " + validStatus + ")");
                        return validStatus;
                    })
                    .map(o -> {
                        WashRequestResponse resp = new WashRequestResponse();
                        resp.setOrderId(o.getId());
                        resp.setCustomerEmail(o.getCustomerEmail());
                        resp.setCarId(o.getCarId());
                        resp.setStatus(o.getStatus());
                        resp.setWasherEmail(o.getWasherEmail());

                        try {
                            PaymentResponse paymentStatus = paymentServiceClient.getPaymentStatus(o.getId());
                            String status = paymentStatus.getStatus();

                            // FIXED: Handle NOT_FOUND status as no payment created yet
                            if ("NOT_FOUND".equals(status)) {
                                resp.setPaymentStatus("NO_PAYMENT");
                               // System.out.println(" No payment created yet for order " + o.getId());
                            } else {
                                resp.setPaymentStatus(status);
                              //  System.out.println(" Payment status for order " + o.getId() + ": " + status);
                            }
                        } catch (Exception ex) {
                            resp.setPaymentStatus("UNKNOWN");
                            System.err.println("⚠️ Could not fetch payment status for order " + o.getId() + ": " + ex.getMessage());
                        }

                        return resp;
                    })
                    .collect(Collectors.toList());

           // System.out.println(" Filtered orders for washer " + washerEmail + ": " + washerOrders.size());
            return washerOrders;

        } catch (FeignException e) {
            System.err.println(" Feign error fetching orders: " + e.status() + " - " + e.getMessage());
            throw new RuntimeException("Failed to fetch orders from booking service: " + e.status(), e);
        } catch (Exception e) {
            System.err.println(" Unexpected error: " + e.getMessage());
            throw new RuntimeException("Failed to get wash requests: " + e.getMessage(), e);
        }
    }

    public OrderResponse acceptWashRequest(Long orderId, String washerEmail) {
      //  System.out.println("Accepting order " + orderId + " for washer " + washerEmail);
        OrderResponse order;

        try {
            order = orderServiceClient.getOrderById(orderId);
        } catch (FeignException e) {
            throw new RuntimeException("Failed to fetch order: " + e.status(), e);
        }

        if (!washerEmail.equals(order.getWasherEmail())) {
            throw new RuntimeException("Order not assigned to this washer");
        }

        if (!"ASSIGNED".equalsIgnoreCase(order.getStatus().trim())) {
            throw new RuntimeException("Order not in assignable state");
        }

        order.setStatus("ACCEPTED");

        try {
            OrderResponse updatedOrder = orderServiceClient.updateOrder(orderId, order);
            publishWasherEvent(orderId, washerEmail, "washer.accepted");
            return updatedOrder;
        } catch (FeignException e) {
            throw new RuntimeException("Failed to update order: " + e.status(), e);
        }
    }

    public OrderResponse rejectWashRequest(Long orderId, String washerEmail) {
        OrderResponse order = orderServiceClient.getOrderById(orderId);

        if (!washerEmail.equals(order.getWasherEmail())) {
            throw new RuntimeException("Washer email does not match");
        }

        if (!"ASSIGNED".equals(order.getStatus())) {
            throw new RuntimeException("Washer status does not match");
        }

        order.setStatus("PENDING");
        publishWasherEvent(orderId, washerEmail, "washer.rejected");
        return orderServiceClient.updateOrder(orderId, order);
    }


    public InvoiceResponse generateInvoice(InvoiceRequest request, String washerEmail) {
        OrderResponse order;

        try {
            order = orderServiceClient.getOrderById(request.getOrderId());
        } catch (FeignException e) {
            throw new RuntimeException("Failed to fetch order: " + e.status(), e);
        }

        if (!"ACCEPTED".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in ACCEPTED status");
        }

        if (!washerEmail.equals(order.getWasherEmail())) {
            throw new RuntimeException("Washer email does not match order");
        }

        // Create and save invoice in Washer Service database
        Invoice invoice = new Invoice();
        invoice.setOrderId(request.getOrderId());
        invoice.setWasherEmail(washerEmail);
        invoice.setAmount(request.getAmount());
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        System.out.println("✅ Saved invoice to Washer Service DB: " + savedInvoice.getId());

        // Process payment through Payment Service
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(request.getOrderId());
        paymentRequest.setAmount(request.getAmount());
        paymentRequest.setInvoiceAmount(request.getAmount());

        PaymentResponse paymentResponse;

        try {
            System.out.println("Calling payment service for customer: " + order.getCustomerEmail());
            System.out.println("Sending PaymentRequest: " + paymentRequest);

            paymentResponse = paymentServiceClient.processPayment(paymentRequest);

            System.out.println("Received PaymentResponse: " + paymentResponse);
            publishWasherEvent(order.getId(), order.getCustomerEmail(), "washer.invoice_generated");
        } catch (FeignException e) {
            System.err.println("Payment service failed: " + e.getMessage());
            throw new RuntimeException("Failed to process payment: " + e.status(), e);
        }

        return new InvoiceResponse(
                request.getOrderId(),
                paymentResponse.getAmount(),
                LocalDateTime.now(),
                paymentResponse.getPaymentId(),
                paymentResponse.getClientSecret(),
                paymentResponse.getPaymentUrl()
        );
    }

    public OrderResponse markBookingAsCompleted(Long orderId, String washerEmail) {
        try {
            System.out.println("markBookingAsCompleted called with orderId: " + orderId + ", washerEmail: " + washerEmail);

            OrderResponse order = orderServiceClient.getOrderById(orderId);

            if (!washerEmail.equals(order.getWasherEmail())) {
                throw new RuntimeException("Order not assigned to this washer.");
            }

            // Time-based completion validation
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime allowedCompletionTime;

            if (order.getScheduledTime() == null) {
                // Wash Now: Can be marked complete after 1 hour from creation
                allowedCompletionTime = order.getCreatedAt().plusHours(1);
                if (now.isBefore(allowedCompletionTime)) {
                    throw new RuntimeException("Wash Now orders can only be marked complete after 1 hour. Please wait until " + allowedCompletionTime);
                }
            } else {
                // Scheduled: Can be marked complete after scheduled time
                allowedCompletionTime = order.getScheduledTime();
                if (now.isBefore(allowedCompletionTime)) {
                    throw new RuntimeException("Scheduled orders can only be marked complete after scheduled time: " + allowedCompletionTime);
                }
            }

            PaymentResponse paymentStatus = paymentServiceClient.getPaymentStatus(orderId);
            System.out.println("service me payment status" + paymentStatus);
            order.setPaymentStatus(paymentStatus.getStatus());

            System.out.println("Payment response is: " + paymentStatus);

            order.setStatus("COMPLETED");
            OrderResponse updatedOrder = orderServiceClient.updateOrder(orderId, order);

            publishWasherEvent(orderId, order.getCustomerEmail(), "order.completed");

            return updatedOrder;
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Order not found: " + e.getMessage());
        } catch (FeignException e) {
            throw new RuntimeException("Error in payment service: " + e.status(), e);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }
    }

    private void publishWasherEvent(Long orderId, String washerEmail, String eventType) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("event", eventType);
            event.put("orderId", orderId);
            event.put("washerEmail", washerEmail);
            rabbitTemplate.convertAndSend("carwash.events", "notification." + eventType, objectMapper.writeValueAsString(event));
            System.out.println("Published event: " + eventType + " for order " + orderId);
        } catch (Exception e) {
            System.err.println(" Error publishing event " + eventType + ": " + e.getMessage());
        }
    }

//    Get all invoices generated by a specific washer

    public List<InvoiceResponse> getWasherInvoices(String washerEmail) {
        try {
            List<Invoice> invoices = invoiceRepository.findAll().stream()
                    .filter(invoice -> washerEmail.equals(invoice.getWasherEmail()))
                    .collect(Collectors.toList());

            return invoices.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch washer invoices: " + e.getMessage(), e);
        }
    }

    /**
     * Dashboard analytics methods for washer statistics
     */
    public long getPendingOrdersCount(String washerEmail) {
        try {
            List<OrderResponse> orders = orderServiceClient.getCurrentOrders();
            return orders.stream()
                    .filter(o -> washerEmail.equals(o.getWasherEmail()))
                    .filter(o -> "ASSIGNED".equalsIgnoreCase(o.getStatus()) || "ACCEPTED".equalsIgnoreCase(o.getStatus()))
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    public long getCompletedTodayCount(String washerEmail) {
        return 3; // Mock data - requires date filtering
    }

    public double getTodaysEarnings(String washerEmail) {
        return 2.0; // Mock data - requires Payment Service integration
    }

    public long getTotalCustomersCount(String washerEmail) {
        return 3; // Mock data - requires unique customer count
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        if (invoice == null) {
            throw new RuntimeException("Invoice not found in database");
        }

        InvoiceResponse response = new InvoiceResponse();
        response.setOrderId(invoice.getOrderId());
        response.setAmount(invoice.getAmount());
        response.setCreatedAt(invoice.getCreatedAt());
        return response;
    }
}
