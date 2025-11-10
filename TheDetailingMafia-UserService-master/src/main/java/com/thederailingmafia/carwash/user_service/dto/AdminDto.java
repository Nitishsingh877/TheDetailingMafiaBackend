package com.thederailingmafia.carwash.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private  String name;
    private String email;
    private String password;
    private  String userRole;

    public AdminDto(UserDto userDto) {
    }
}
