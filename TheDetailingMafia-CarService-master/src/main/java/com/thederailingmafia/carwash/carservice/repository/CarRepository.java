package com.thederailingmafia.carwash.carservice.repository;

import com.thederailingmafia.carwash.carservice.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car,Long> {


    List<Car> findByCustomerEmail(String customerEmail);


    long countByCustomerEmail(String customerEmail);



}
