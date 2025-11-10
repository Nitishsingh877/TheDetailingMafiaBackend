package com.thederailingmafia.carwash.user_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "washer")
@NoArgsConstructor
public class Washer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Washer_id;
    private String WasherName;
    private String washerEmail;
    private boolean isActive;


    @OneToOne
    @JoinColumn(name = "User_id",nullable = false,unique = true)
    private UserModel user;

    private Long phoneNumber;
    private String address;

    public Washer(String WasherName, String WasherEmail, Long phoneNumber, String address,boolean IsActive) {
        this.WasherName = WasherName;
        this.washerEmail = WasherEmail;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isActive = IsActive;
    }

}
