package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.StringResponse;
import com.example.demo.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
	@Mock
	private AuthService authService;
	@InjectMocks
	private AuthController authController;

	@Test
	void register_shouldRegisterUser() {
		RegisterRequest request = new RegisterRequest();
		StringResponse registerResponse = new StringResponse("User registered successfully");
		when(authService.register(request)).thenReturn(registerResponse);
		ResponseEntity<StringResponse> response = authController.register(request);
		assertNotNull(response);
		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals("User registered successfully", response.getBody().getMessage());
		verify(authService).register(request);
	}

	@Test
	void login_shouldReturnAuthResponse() {
		LoginRequest request = new LoginRequest();
		AuthResponse authResponse = new AuthResponse();
		when(authService.login(request)).thenReturn(authResponse);
		ResponseEntity<AuthResponse> response = authController.login(request);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(authResponse, response.getBody());
		verify(authService).login(request);
	}

	@Test
	void refreshToken_shouldReturnAuthResponse() {
		RefreshTokenRequest request = new RefreshTokenRequest();
		AuthResponse authResponse = new AuthResponse();
		when(authService.refreshToken(request)).thenReturn(authResponse);
		ResponseEntity<AuthResponse> response = authController.refreshToken(request);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(authResponse, response.getBody());
		verify(authService).refreshToken(request);
	}
}