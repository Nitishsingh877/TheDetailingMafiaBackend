package com.thederailingmafia.carwash.user_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("mySecretKeyForJWTTokenGenerationAndValidation12345");
    }

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getEmailFromToken_Success() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER");

        String email = jwtUtil.getEmailFromToken(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void getRoleFromToken_Success() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER");

        String role = jwtUtil.getRoleFromToken(token);

        assertEquals("CUSTOMER", role);
    }

    @Test
    void validateToken_Success() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER");

        boolean isValid = jwtUtil.validateToken(token, "test@example.com");

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidEmail() {
        String token = jwtUtil.generateToken("test@example.com", "CUSTOMER");

        boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        assertFalse(isValid);
    }
}
