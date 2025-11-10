package com.thederailingmafia.carwash.carservice.controller;

import com.thederailingmafia.carwash.carservice.dto.CarRequest;
import com.thederailingmafia.carwash.carservice.exception.CarNotFoundException;
import com.thederailingmafia.carwash.carservice.exception.UserNotFoundException;
import com.thederailingmafia.carwash.carservice.model.Car;
import com.thederailingmafia.carwash.carservice.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(description = "car Service",name = "carService")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

//    total car for dashboard
    @GetMapping("/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Long> getTotalCarsCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String customerEmail = auth.getName();
        long count = carService.getCustomerCarsCount(customerEmail);
        return ResponseEntity.ok(count);
    }


    @PostMapping("/add")
    @Operation(summary = "Add a new car", description = "add a car")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Car> addCar(@RequestBody @Valid CarRequest request) throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }

        Car response = carService.addCar(request, userEmail.trim());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary ="Get car Details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Car> getCar(@PathVariable Long id) throws CarNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid car ID: " + id);
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }

        Car response = carService.getCar(id, userEmail.trim());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<Car> getAllCars() throws CarNotFoundException, UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email is required");
        }

        List<Car> cars = carService.getAllCars(userEmail.trim());
        return cars;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update car Details")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Car> updateCar(@PathVariable Long id,
                                         @RequestBody @Valid CarRequest request) throws UserNotFoundException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = auth.getName();

        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid car ID for update: " + id);
        }
        if (request == null) {
            throw new RuntimeException("Car update request cannot be null");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email is required for car update");
        }

        Car response = carService.updateCar(id, request, userEmail.trim());
        return ResponseEntity.status(200).body(response);
    }



}
