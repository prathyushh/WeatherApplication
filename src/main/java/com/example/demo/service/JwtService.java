package com.example.demo.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final SecretKey secretKey = Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key-123456".getBytes());
	private final long expirationTime = 1000 * 60;
	private final long refreshTokenExpirationTime = 1000L * 60 * 60 * 24 * 7;

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

	private boolean isTokenExpired(String token) {
		Date expiration = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload()
				.getExpiration();
		return expiration.before(new Date());
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

	}

}
