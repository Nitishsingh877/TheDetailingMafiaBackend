package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.CustomerDto;
import com.thederailingmafia.carwash.user_service.model.Customer;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public void CreateCustomer(UserModel userModel , CustomerDto customerDto) {
        Customer customer = new Customer(customerDto.getName(), userModel.getAddress(), userModel.getPhoneNumber(), customerDto.getEmail());
        customer.setUser(userModel);
        customer.setUserEmail(userModel.getEmail());
        customerRepository.save(customer);
    }
}
