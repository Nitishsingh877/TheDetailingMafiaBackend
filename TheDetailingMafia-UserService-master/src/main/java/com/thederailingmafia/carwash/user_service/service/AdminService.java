package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.AdminDto;
import com.thederailingmafia.carwash.user_service.dto.CustomerDto;
import com.thederailingmafia.carwash.user_service.model.Admin;
import com.thederailingmafia.carwash.user_service.model.Customer;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.repository.AdminRepository;
import com.thederailingmafia.carwash.user_service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Transactional
    public void createAdmin( UserModel userModel, AdminDto adminDto) {
       Admin admin = new Admin();
       admin.setName( userModel.getName());
       admin.setEmail( userModel.getEmail());
       admin.setUser(userModel);
       adminRepository.save(admin);
    }
}
