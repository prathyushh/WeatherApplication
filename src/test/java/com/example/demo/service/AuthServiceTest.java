package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.exception.InvalidRefreshTokenException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;

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
	void register_shouldRegisterUser() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("admin");
		request.setPassword("password");
		when(repository.findByUsername("admin")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		authService.register(request);
		verify(repository).findByUsername("admin");
		verify(passwordEncoder).encode("password");
		verify(repository).save(any(User.class));
		verify(auditService).recordAudit("admin", "REGISTER", "User registered successfully");
	}

	@Test
	void register_shouldThrowException_whenUserAlreadyExists() {
		RegisterRequest request = new RegisterRequest();
		request.setUsername("admin");
		request.setPassword("password");
		User existingUser = new User();
		existingUser.setUsername("admin");
		when(repository.findByUsername("admin")).thenReturn(Optional.of(existingUser));
		assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
		verify(repository).findByUsername("admin");
		verifyNoInteractions(passwordEncoder);
		verifyNoInteractions(auditService);
	}

	@Test
	void login_shouldReturnAuthResponse() {
		LoginRequest request = new LoginRequest();
		request.setUsername("admin");
		request.setPassword("password");
		when(manager.authenticate(any())).thenReturn(authentication);
		when(authentication.getPrincipal()).thenReturn(userDetails);
		when(jwtService.generateToken(userDetails)).thenReturn("access-token");
		when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");
		AuthResponse response = authService.login(request);
		assertNotNull(response);
		assertEquals("access-token", response.getAccessToken());
		assertEquals("refresh-token", response.getRefreshToken());
		verify(manager).authenticate(any());
		verify(jwtService).generateToken(userDetails);
		verify(jwtService).generateRefreshToken(userDetails);
	}

	@Test
	void refreshToken_shouldReturnNewAccessToken() {
		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setRefreshToken("old-refresh-token");
		when(jwtService.extractUsername("old-refresh-token")).thenReturn("admin");
		when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
		when(jwtService.validateToken("old-refresh-token", userDetails)).thenReturn(true);
		when(jwtService.generateToken(userDetails)).thenReturn("new-access-token");
		AuthResponse response = authService.refreshToken(request);
		assertNotNull(response);
		assertEquals("new-access-token", response.getAccessToken());
		assertEquals("old-refresh-token", response.getRefreshToken());
		verify(jwtService).extractUsername("old-refresh-token");
		verify(userDetailsService).loadUserByUsername("admin");
		verify(jwtService).validateToken("old-refresh-token", userDetails);
		verify(jwtService).generateToken(userDetails);
	}

	@Test
	void refreshToken_shouldThrowException_whenTokenIsInvalid() {
		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setRefreshToken("invalid-token");
		when(jwtService.extractUsername("invalid-token")).thenReturn("admin");
		when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
		when(jwtService.validateToken("invalid-token", userDetails)).thenReturn(false);
		assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(request));
		verify(jwtService).validateToken("invalid-token", userDetails);
	}
}