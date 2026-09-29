package com.example.demo.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey secretKey = Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key-123456".getBytes());
	private final long expirationTime = 1000 * 60 * 5;
	private final long refreshTokenExpirationTime = 1000 * 60 * 60 * 12;

	public String generateToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(secretKey).compact();
	}

	public String generateRefreshToken(UserDetails userDetails) {
		return Jwts.builder().subject(userDetails.getUsername()).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime)).signWith(secretKey)
				.compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		try {
			String username = extractUsername(token);
			return username.equals(userDetails.getUsername());
		} catch (ExpiredJwtException ex) {
			return false;
		}
	}
}