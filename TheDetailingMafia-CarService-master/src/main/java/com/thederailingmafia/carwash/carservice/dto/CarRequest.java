package com.thederailingmafia.carwash.carservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CarRequest {


    @NotBlank(message = "Car brand is required")
    @Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
    private String brand;


    @NotBlank(message = "Car model is required")
    @Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
    private String model;


    @NotBlank(message = "License plate number is required")
    @Size(min = 3, max = 20, message = "License plate must be between 3 and 20 characters")
    private String licenseNumberPlate;
}
