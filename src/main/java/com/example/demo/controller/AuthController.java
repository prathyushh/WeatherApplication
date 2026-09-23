package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;
	private final AuthenticationManager manager;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public AuthController(PasswordEncoder passwordEncoder, UserRepository repository, AuthenticationManager manager,
			JwtService jwtService, UserDetailsService userDetailsService) {
		this.passwordEncoder = passwordEncoder;
		this.repository = repository;
		this.manager = manager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(encodedPassword);
		user.setRole(Role.USER);
		repository.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
		Authentication authentication = manager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String token = jwtService.generateToken(userDetails);
		String refreshToken = jwtService.generateRefreshToken(userDetails);
		AuthResponse authResponse = new AuthResponse(token, refreshToken);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {

		String refreshToken = request.getRefreshToken();

		String username = jwtService.extractUsername(refreshToken);

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (!jwtService.validateToken(refreshToken, userDetails)) {
			return ResponseEntity.status(401).build();
		}

		String newAccessToken = jwtService.generateToken(userDetails);

		AuthResponse response = new AuthResponse(newAccessToken, refreshToken);

		return ResponseEntity.ok(response);
	}
}