package com.thederailingmafia.carwash.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    String name;
    String email;
    public CustomerDto(UserDto userDto){
        this.name = userDto.getName();
        this.email = userDto.getEmail();
    }
}
