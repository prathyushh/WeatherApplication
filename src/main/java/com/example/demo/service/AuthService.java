package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;
	private final AuditService auditService;
	private final AuthenticationManager manager;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public AuthService(PasswordEncoder passwordEncoder, UserRepository repository, AuditService auditService,
			AuthenticationManager manager, JwtService jwtService, UserDetailsService userDetailsService) {

		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
		this.auditService = auditService;
		this.manager = manager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Transactional
	public void registerUser(RegisterRequest request) {

		try {
			String encodedPassword = passwordEncoder.encode(request.getPassword());

			User user = new User();
			user.setUsername(request.getUsername());
			user.setPassword(encodedPassword);
			user.setRole(Role.USER);

			repository.save(user);

			log.info("User registered successfully: {}", user.getUsername());

			auditService.logAction(user.getUsername(), "REGISTER", "User registered successfully");

		} catch (Exception ex) {

			log.error("User registration failed: {}", request.getUsername(), ex);

			throw ex;
		}
	}

	public AuthResponse loginUser(LoginRequest request) {

		try {
			Authentication authentication = manager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			String token = jwtService.generateToken(userDetails);

			String refreshToken = jwtService.generateRefreshToken(userDetails);

			log.info("User authenticated successfully: {}", request.getUsername());

			return new AuthResponse(token, refreshToken);

		} catch (Exception ex) {

			log.error("User authentication failed: {}", request.getUsername(), ex);

			throw ex;
		}
	}

	public AuthResponse refreshToken(RefreshTokenRequest request) {

		try {
			String refreshToken = request.getRefreshToken();

			String username = jwtService.extractUsername(refreshToken);

			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (!jwtService.validateToken(refreshToken, userDetails)) {
				throw new RuntimeException("Invalid refresh token");
			}

			String newAccessToken = jwtService.generateToken(userDetails);

			log.info("Access token refreshed successfully: {}", username);

			return new AuthResponse(newAccessToken, refreshToken);

		} catch (Exception ex) {

			log.error("Token refresh failed", ex);

			throw ex;
		}
	}
}