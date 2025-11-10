//package com.thederailingmafia.carwash.carservice.feign;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//
//@FeignClient(name = "user-service", url = "http://localhost:8081")
//public interface UserServiceClient {
//
//    /**
//     * Validates if a customer exists by email
//     * Used before creating/updating car records to ensure referential integrity
//     */
//    @GetMapping("/api/users/validate/customer/{email}")
//    Boolean validateCustomerExists(@PathVariable("email") String email);
//
//    /**
//     * Get customer details by email for car operations
//     * Used when detailed customer information is needed
//     */
//    @GetMapping("/api/users/customer/email/{email}")
//    Object getCustomerByEmail(@PathVariable("email") String email);
//}