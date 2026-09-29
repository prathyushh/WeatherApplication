package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;

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
	void validateToken_shouldReturnFalse_whenTokenIsExpired() throws Exception {
		UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();
		SecretKey secretKey = getSecretKey();
		String expiredToken = Jwts.builder().subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis() - 10_000))
				.expiration(new Date(System.currentTimeMillis() - 1_000)).signWith(secretKey).compact();
		boolean result = jwtService.validateToken(expiredToken, userDetails);
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

	private SecretKey getSecretKey() throws Exception {
		Field field = JwtService.class.getDeclaredField("secretKey");
		field.setAccessible(true);
		return (SecretKey) field.get(jwtService);
	}
}