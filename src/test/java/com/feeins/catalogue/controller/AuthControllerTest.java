package com.feeins.catalogue.controller;

import com.feeins.catalogue.dto.AuthResponse;
import com.feeins.catalogue.dto.LoginRequest;
import com.feeins.catalogue.dto.RegisterRequest;
import com.feeins.catalogue.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password");
        registerRequest = new RegisterRequest();
        registerRequest.setNom("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setMotDePasse("password");
        authResponse = AuthResponse.builder()
                .token("jwt-token")
                .id(1L)
                .nom("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void login_Success() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void login_Failure() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenThrow(new RuntimeException("Invalid credentials"));

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthController.ErrorResponse);
        AuthController.ErrorResponse error = (AuthController.ErrorResponse) response.getBody();
        assertEquals("Email ou mot de passe incorrect", error.message());
    }

    @Test
    void register_Success() {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    void register_Failure() {
        // Arrange
        when(authService.register(any(RegisterRequest.class))).thenThrow(new RuntimeException("Email already exists"));

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthController.ErrorResponse);
        AuthController.ErrorResponse error = (AuthController.ErrorResponse) response.getBody();
        assertEquals("Email already exists", error.message());
    }
}