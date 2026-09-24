package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.InvalidRefreshTokenException;
import com.example.demo.exception.UserAlreadyExistException;
import com.example.demo.repository.UserRepository;

import io.jsonwebtoken.JwtException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private UserRepository repository;

	@Mock
	private AuditService auditService;

	@Mock
	private AuthenticationManager manager;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private Authentication authentication;

	@Mock
	private UserDetails userDetails;

	@InjectMocks
	private AuthService authService;

	@Test
	void registerUser_shouldRegisterUser_whenUserDoesNotExist() {

		RegisterRequest request = new RegisterRequest();

		request.setUsername("admin");
		request.setPassword("password123");
		request.setRole(Role.ADMIN);

		when(repository.findByUsername("admin")).thenReturn(Optional.empty());

		when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

		authService.registerUser(request);

		verify(repository).findByUsername("admin");

		verify(passwordEncoder).encode("password123");

		verify(repository).save(any(User.class));

		verify(auditService).logAction("admin", "REGISTER", "User registered successfully");
	}

	@Test
	void registerUser_shouldThrowException_whenUserAlreadyExists() {

		RegisterRequest request = new RegisterRequest();

		request.setUsername("admin");
		request.setPassword("password123");
		request.setRole(Role.ADMIN);

		User existingUser = new User();

		existingUser.setUsername("admin");

		when(repository.findByUsername("admin")).thenReturn(Optional.of(existingUser));

		UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class,
				() -> authService.registerUser(request));

		assertEquals("User already exists", exception.getMessage());

		verify(repository).findByUsername("admin");

		verify(repository, never()).save(any(User.class));

		verify(passwordEncoder, never()).encode(any(String.class));

		verify(auditService, never()).logAction(any(), any(), any());
	}

	@Test
	void loginUser_shouldReturnTokens_whenCredentialsAreValid() {

		LoginRequest request = new LoginRequest();

		request.setUsername("admin");
		request.setPassword("password123");

		when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

		when(authentication.getPrincipal()).thenReturn(userDetails);

		when(jwtService.generateToken(userDetails)).thenReturn("access-token");

		when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

		AuthResponse response = authService.loginUser(request);

		assertEquals("access-token", response.getAccessToken());

		assertEquals("refresh-token", response.getRefreshToken());

		verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));

		verify(jwtService).generateToken(userDetails);

		verify(jwtService).generateRefreshToken(userDetails);
	}

	@Test
	void loginUser_shouldThrowException_whenAuthenticationFails() {

		LoginRequest request = new LoginRequest();

		request.setUsername("admin");
		request.setPassword("wrongPassword");

		RuntimeException exception = new RuntimeException("Invalid credentials");

		when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(exception);

		RuntimeException result = assertThrows(RuntimeException.class, () -> authService.loginUser(request));

		assertEquals("Invalid credentials", result.getMessage());

		verify(manager).authenticate(any(UsernamePasswordAuthenticationToken.class));

		verify(jwtService, never()).generateToken(any(UserDetails.class));

		verify(jwtService, never()).generateRefreshToken(any(UserDetails.class));
	}

	@Test
	void refreshToken_shouldReturnNewAccessToken_whenRefreshTokenIsValid() {

		RefreshTokenRequest request = new RefreshTokenRequest();

		request.setRefreshToken("old-refresh-token");

		when(jwtService.extractUsername("old-refresh-token")).thenReturn("admin");

		when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

		when(jwtService.validateToken("old-refresh-token", userDetails)).thenReturn(true);

		when(jwtService.generateToken(userDetails)).thenReturn("new-access-token");

		AuthResponse response = authService.refreshToken(request);

		assertEquals("new-access-token", response.getAccessToken());

		assertEquals("old-refresh-token", response.getRefreshToken());

		verify(jwtService).extractUsername("old-refresh-token");

		verify(userDetailsService).loadUserByUsername("admin");

		verify(jwtService).validateToken("old-refresh-token", userDetails);

		verify(jwtService).generateToken(userDetails);
	}

	@Test
	void refreshToken_shouldThrowException_whenRefreshTokenIsInvalid() {

		RefreshTokenRequest request = new RefreshTokenRequest();

		request.setRefreshToken("invalid-refresh-token");

		when(jwtService.extractUsername("invalid-refresh-token")).thenReturn("admin");

		when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);

		when(jwtService.validateToken("invalid-refresh-token", userDetails)).thenReturn(false);

		InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
				() -> authService.refreshToken(request));

		assertEquals("Invalid refresh token", exception.getMessage());

		verify(jwtService).extractUsername("invalid-refresh-token");

		verify(userDetailsService).loadUserByUsername("admin");

		verify(jwtService).validateToken("invalid-refresh-token", userDetails);

		verify(jwtService, never()).generateToken(any(UserDetails.class));
	}

	@Test
	void refreshToken_shouldThrowInvalidRefreshTokenException_whenJwtExceptionOccurs() {

		RefreshTokenRequest request = new RefreshTokenRequest();

		request.setRefreshToken("bad-token");

		when(jwtService.extractUsername("bad-token")).thenThrow(new JwtException("Invalid JWT") {
		});

		InvalidRefreshTokenException exception = assertThrows(InvalidRefreshTokenException.class,
				() -> authService.refreshToken(request));

		assertEquals("Invalid refresh token", exception.getMessage());

		verify(jwtService).extractUsername("bad-token");

		verify(userDetailsService, never()).loadUserByUsername(any(String.class));

		verify(jwtService, never()).generateToken(any(UserDetails.class));
	}
}