package com.thederailingmafia.carwash.user_service.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("User not found");

        ResponseEntity<String> response = handler.handleUserNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void handleEmailExistException() {
        EmailExistException ex = new EmailExistException("Email already exists");

        ResponseEntity<String> response = handler.handleEmailExistException(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    void handleRoleNotFoundException() {
        RoleNotFoundException ex = new RoleNotFoundException("Role not found");

        ResponseEntity<String> response = handler.handleRoleNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Role not found", response.getBody());
    }

    @Test
    void handlePasswordNotFoundException() {
        PassswordNotFoundException ex = new PassswordNotFoundException("Password not found");

        ResponseEntity<String> response = handler.handlePasswordNotFoundException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Password not found", response.getBody());
    }
}
