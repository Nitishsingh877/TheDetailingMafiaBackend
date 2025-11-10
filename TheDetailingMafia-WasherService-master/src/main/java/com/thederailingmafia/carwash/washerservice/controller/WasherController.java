package com.thederailingmafia.carwash.washerservice.controller;

import com.thederailingmafia.carwash.washerservice.client.PaymentServiceClient;
import com.thederailingmafia.carwash.washerservice.dto.*;
import com.thederailingmafia.carwash.washerservice.service.WasherService;
import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/washer")
public class WasherController {

    @Autowired
    private WasherService washerService;

    @Autowired
    private PaymentServiceClient paymentServiceClient;

    @GetMapping("/health")
    public String health() {
        return "Washer Service is up and running";
    }


    @GetMapping("/request")
    @PreAuthorize("hasRole('WASHER')")
    public List<WashRequestResponse> getRequests() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
       // System.out.println("washerEmail: " + washerEmail);
        List<WashRequestResponse> request = washerService.getWashRequest(washerEmail);

       // System.out.println(request);
        return request;
    }

    @PostMapping("/accept/{orderId}")
    @PreAuthorize("hasRole('WASHER')")
    public OrderResponse acceptWashRequest(@PathVariable Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        OrderResponse response = washerService.acceptWashRequest(orderId, washerEmail);
        return response;
    }

    @PostMapping("/decline/{orderId}")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<OrderResponse> declineWashRequest(@PathVariable Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        OrderResponse response = washerService.rejectWashRequest(orderId, washerEmail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/invoice")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String washerEmail = auth.getName();
           // System.out.println("Creating invoice for washer:" + washerEmail);
            InvoiceResponse response = washerService.generateInvoice(request, washerEmail);
           // System.out.println(response);
            return ResponseEntity.ok(response);

        } catch (FeignException.Unauthorized e) {
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        } catch (FeignException.Forbidden e) {
            return ResponseEntity.status(403).body("Access denied: " + e.getMessage());
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body("Payment service error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }


    @PostMapping("/done/{orderId}")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<?> markAsCompleted(@PathVariable Long orderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();

        try {
            // ✅ Check payment status before completing the order
            PaymentResponse paymentStatus = paymentServiceClient.getPaymentStatus(orderId);
           // System.out.println("payment stauts"  + paymentStatus);

            if (!"SUCCESS".equalsIgnoreCase(paymentStatus.getStatus()) && !"succeeded".equalsIgnoreCase(paymentStatus.getStatus())) {
                return ResponseEntity.badRequest().body(" Payment not completed. Cannot mark booking as done.");
            }

            // ✅ Proceed to update booking status
            OrderResponse response = washerService.markBookingAsCompleted(orderId, washerEmail);
            return ResponseEntity.ok(response);

        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(404).body(" Order not found: " + e.getMessage());
        } catch (FeignException e) {
            return ResponseEntity.status(e.status()).body(" Payment service error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(" Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/invoices")
    @PreAuthorize("hasRole('WASHER')")
    @Operation(summary = "Get washer's invoice history")
    public ResponseEntity<?> getWasherInvoices() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String washerEmail = auth.getName();

            List<InvoiceResponse> invoices = washerService.getWasherInvoices(washerEmail);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch invoices: " + e.getMessage());
        }
    }

//   dashboard stats
    @GetMapping("/orders/pending/count")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<Long> getPendingOrdersCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        long count = washerService.getPendingOrdersCount(washerEmail);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/orders/completed/today")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<Long> getCompletedTodayCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        long count = washerService.getCompletedTodayCount(washerEmail);
        return ResponseEntity.ok(count);
    }
    @GetMapping("/earnings/today")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<Double> getTodaysEarnings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        double earnings = washerService.getTodaysEarnings(washerEmail);
        return ResponseEntity.ok(earnings);
    }


    @GetMapping("/customers/count")
    @PreAuthorize("hasRole('WASHER')")
    public ResponseEntity<Long> getTotalCustomersCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String washerEmail = auth.getName();
        long count = washerService.getTotalCustomersCount(washerEmail);
        return ResponseEntity.ok(count);
    }
}
