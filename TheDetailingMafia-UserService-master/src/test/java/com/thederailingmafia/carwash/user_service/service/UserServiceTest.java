package com.thederailingmafia.carwash.user_service.service;

import com.thederailingmafia.carwash.user_service.model.UserModel;
import com.thederailingmafia.carwash.user_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class) //enables mockito
class UserServiceTest {


    @Mock //mock the repository create a non real repo
    private UserRepository userRepository;

    @InjectMocks //inject mock into services
    private UserService userService;

    @Test
    void testFindByEmail() {
//arrange
        UserModel mockUser = new UserModel();
        mockUser.setEmail("nitish8989@gmail.com");
        mockUser.setName("Nitish 8989");

        Mockito.when(userRepository.findByEmail("nitish8989@gmail.com"))
                .thenReturn(Optional.of(mockUser));


        //act
        UserModel result = userService.findUserByEmail("nitish8989@gmail.com");

        //assert
        assertNotNull(result);
        assertEquals("Nitish 8989", result.getName(),"Username not found");
    }
        @Test
        void testFindUserByEmail_NotFound() {
            // Arrange
            Mockito.when(userRepository.findByEmail("notfound@example.com"))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> userService.findUserByEmail("notfound@example.com"));
        }
    }
