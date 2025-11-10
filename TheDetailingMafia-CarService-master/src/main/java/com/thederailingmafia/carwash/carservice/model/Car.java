package com.thederailingmafia.carwash.carservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;


    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    private String brand;
    private String model;
    private String licenseNumberPlate;

    public Car(String brand, String model, String licenseNumberPlate) {
        this.brand = brand;
        this.model = model;
        this.licenseNumberPlate = licenseNumberPlate;
    }


}

