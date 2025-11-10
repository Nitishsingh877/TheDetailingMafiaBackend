package com.vector_search_service.ai.dto;

import lombok.Data;

@Data
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private String licenseNumberPlate;
}