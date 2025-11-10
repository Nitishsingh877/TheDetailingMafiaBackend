package com.thederailingmafia.carwash.user_service.model;

// REMOVED: JsonBackReference and JsonManagedReference imports - no longer needed
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private UserModel user;


    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    private String name;
    private String address;
    private Long phoneNumber;
    private String email;
    private String password;



    public Customer(String name, String address, Long phoneNumber, String email) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
