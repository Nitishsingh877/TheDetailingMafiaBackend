package com.thederailingmafia.carwash.user_service.dto;

import com.thederailingmafia.carwash.user_service.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class UserProfileResponse {
        private String name;
        private String email;
        private UserRole userRole;
        private String address;
        private long phone_number;

    }

