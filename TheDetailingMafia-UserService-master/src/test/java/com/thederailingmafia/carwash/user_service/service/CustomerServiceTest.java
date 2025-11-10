package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.CustomerDto;
import com.thederailingmafia.carwash.user_service.model.Customer;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void CreateCustomer_Success() {
        UserModel user = new UserModel();
        user.setEmail("test@example.com");
        user.setAddress("123 Street");
        user.setPhoneNumber("1234567890");

        CustomerDto dto = new CustomerDto();
        dto.setName("John Doe");
        dto.setEmail("test@example.com");

        when(customerRepository.save(any(Customer.class))).thenReturn(new Customer());

        customerService.CreateCustomer(user, dto);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
