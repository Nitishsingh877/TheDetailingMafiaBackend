package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.WasherDto;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.Washer;
import com.thederailingmafia.carwash.user_service.repository.WasherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WasherService {

    @Autowired
    private WasherRepository washerRepository;

    public void SaveWasher(UserModel userModel , WasherDto washerDto) {
        Washer washer = new Washer(washerDto.getWasherName(),washerDto.getWasherEmail(),null,null,washerDto.getIsActive());
        washer.setUser(userModel);
        washerRepository.save(washer);
    }


    public Washer updateWasherStatus(String mail, boolean isActive) {
        Washer washer = washerRepository.findByWasherEmail(mail)
                .orElseThrow(() -> new RuntimeException("Washer not found"));
        washer.setActive(isActive);
        return washerRepository.save(washer);
    }



}
