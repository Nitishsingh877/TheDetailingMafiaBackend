package com.thederailingmafia.carwash.user_service.repository;

import com.thederailingmafia.carwash.user_service.model.Washer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WasherRepository extends JpaRepository<Washer,Long> {


    Optional<Washer> findByWasherEmail(String washerEmail);
    List<Washer> findByIsActiveTrue();


}
