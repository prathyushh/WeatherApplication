package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

	private final JwtService jwtService = new JwtService();

	@Test
	void generateToken_shouldGenerateToken() {

		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();

		String token = jwtService.generateToken(userDetails);

		assertNotNull(token);

		assertFalse(token.isEmpty());
	}

	@Test
	void extractUsername_shouldReturnUsername() {

		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();

		String token = jwtService.generateToken(userDetails);

		String username = jwtService.extractUsername(token);

		assertEquals("admin", username);
	}

	@Test
	void validateToken_shouldReturnTrue_whenTokenIsValid() {

		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();

		String token = jwtService.generateToken(userDetails);

		boolean result = jwtService.validateToken(token, userDetails);

		assertTrue(result);
	}

	@Test
	void validateToken_shouldReturnFalse_whenUsernameDoesNotMatch() {

		UserDetails admin = User.withUsername("admin").password("password").roles("ADMIN").build();

		UserDetails user = User.withUsername("user").password("password").roles("USER").build();

		String token = jwtService.generateToken(admin);

		boolean result = jwtService.validateToken(token, user);

		assertFalse(result);
	}

	@Test
	void generateRefreshToken_shouldGenerateToken() {

		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();

		String refreshToken = jwtService.generateRefreshToken(userDetails);

		assertNotNull(refreshToken);

		assertFalse(refreshToken.isEmpty());
	}

	@Test
	void extractUsername_shouldWorkForRefreshToken() {

		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();

		String refreshToken = jwtService.generateRefreshToken(userDetails);

		String username = jwtService.extractUsername(refreshToken);

		assertEquals("admin", username);
	}
}