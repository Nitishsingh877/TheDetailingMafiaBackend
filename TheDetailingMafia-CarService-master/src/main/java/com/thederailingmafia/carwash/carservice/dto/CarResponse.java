package com.thederailingmafia.carwash.carservice.dto;

import lombok.Data;

@Data
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private String licenseNumberPlate;


}
