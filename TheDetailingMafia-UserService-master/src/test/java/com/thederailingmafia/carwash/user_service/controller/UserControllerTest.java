package com.thederailingmafia.carwash.user_service.controller;

import com.thederailingmafia.carwash.user_service.dto.LoginResponseDto;
import com.thederailingmafia.carwash.user_service.dto.UserDto;
import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.model.UserRole;
import com.thederailingmafia.carwash.user_service.repository.UserRepository;
import com.thederailingmafia.carwash.user_service.service.UserService;
import com.thederailingmafia.carwash.user_service.util.JwtUtil;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;



@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;


    @BeforeEach
    void setUp() {
        System.out.println("testing is going on");
    }
    @Test
    void testHealthReturnsOk() {
        String response = userController.health();
        assertEquals("OK",response);
    }
//    @Test
//    void testHealthReturnsOkOrNot() {
//        String response = userController.health();
//        assertEquals("Ok",response);
//    }

    @Test
    void testSignUp() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");

        UserModel userModel = new UserModel();
        userModel.setEmail(userDto.getEmail());
        userModel.setUserRole(UserRole.CUSTOMER);

        when(userService.saveUser(any(UserDto.class), eq("Email"))).thenReturn(userModel);
        when(jwtUtil.generateToken(anyString(),anyString())).thenReturn("mock-token");

        ResponseEntity<?> response = userController.signUp(userDto);
//        System.out.println(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponseDto body = (LoginResponseDto) response.getBody();
        assertNotNull(body);
        assertEquals("mock-token", body.getToken());
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Given
        UserDto userDto = new UserDto();
        userDto.setEmail("login@example.com");
        userDto.setPassword("secure123");

        LoginResponseDto mockResponse = new LoginResponseDto("fake-jwt-token", userDto);

        // When
        when(userService.loginUser("login@example.com", "secure123")).thenReturn(mockResponse);

        // Then
        LoginResponseDto result = userController.login(userDto);

        assertNotNull(result);
        assertEquals("fake-jwt-token", result.getToken());
        assertEquals("login@example.com", result.getUser().getEmail());
    }


}