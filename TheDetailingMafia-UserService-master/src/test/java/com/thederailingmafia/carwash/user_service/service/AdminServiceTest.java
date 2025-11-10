package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.dto.AdminDto;
import com.thederailingmafia.carwash.user_service.model.Admin;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void createAdmin_Success() {
        UserModel user = new UserModel();
        user.setName("Admin User");
        user.setEmail("admin@example.com");

        AdminDto dto = new AdminDto();

        when(adminRepository.save(any(Admin.class))).thenReturn(new Admin());

        adminService.createAdmin(user, dto);

        verify(adminRepository, times(1)).save(any(Admin.class));
    }
}
