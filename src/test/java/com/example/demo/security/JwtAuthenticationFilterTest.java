package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.demo.constant.ExceptionMessages;
import com.example.demo.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
	@Mock
	private JwtService jwtService;
	@Mock
	private UserDetailsService userDetailsService;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
		userDetails = new User("testuser", "password", List.of());
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldContinueFilterChainWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
		when(request.getHeader("Authorization")).thenReturn(null);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldContinueFilterChainWhenAuthorizationHeaderIsNotBearer() throws ServletException, IOException {
		when(request.getHeader("Authorization")).thenReturn("Basic abc123");
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldAuthenticateUserWhenJwtIsValid() throws ServletException, IOException {
		String jwt = "valid.jwt.token";
		String username = "testuser";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(username);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(jwtService.validateToken(jwt, userDetails)).thenReturn(true);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertEquals(username, authentication.getName());
		assertEquals(userDetails, authentication.getPrincipal());
		assertEquals(Set.copyOf(userDetails.getAuthorities()), Set.copyOf(authentication.getAuthorities()));
		verify(jwtService).extractUsername(jwt);
		verify(userDetailsService).loadUserByUsername(username);
		verify(jwtService).validateToken(jwt, userDetails);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenUsernameIsNull() throws ServletException, IOException {
		String jwt = "valid.jwt.token";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(null);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(jwtService).extractUsername(jwt);
		verifyNoInteractions(userDetailsService);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenAuthenticationAlreadyExists() throws ServletException, IOException {
		String jwt = "valid.jwt.token";
		UsernamePasswordAuthenticationToken existingAuthentication = new UsernamePasswordAuthenticationToken(
				"existingUser", null, List.of());
		SecurityContextHolder.getContext().setAuthentication(existingAuthentication);
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn("testuser");
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		assertEquals(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());
		verify(jwtService).extractUsername(jwt);
		verifyNoInteractions(userDetailsService);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenJwtValidationFails() throws ServletException, IOException {
		String jwt = "invalid.jwt.token";
		String username = "testuser";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		when(jwtService.extractUsername(jwt)).thenReturn(username);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(jwtService.validateToken(jwt, userDetails)).thenReturn(false);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(jwtService).extractUsername(jwt);
		verify(userDetailsService).loadUserByUsername(username);
		verify(jwtService).validateToken(jwt, userDetails);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldReturnUnauthorizedWhenJwtIsExpired() throws ServletException, IOException {
		String jwt = "expired.jwt.token";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		ExpiredJwtException exception = mock(ExpiredJwtException.class);
		when(jwtService.extractUsername(jwt)).thenThrow(exception);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType("application/json");
		verify(response).getWriter();
		assertEquals("{\"error\":\"" + ExceptionMessages.JWT_TOKEN_EXPIRED + "\"}", stringWriter.toString());
		verify(filterChain, never()).doFilter(request, response);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldReturnUnauthorizedWhenJwtIsInvalid() throws ServletException, IOException {
		String jwt = "invalid.jwt.token";
		when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
		JwtException exception = mock(JwtException.class);
		when(jwtService.extractUsername(jwt)).thenThrow(exception);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		when(response.getWriter()).thenReturn(printWriter);
		jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType("application/json");
		verify(response).getWriter();
		assertEquals("{\"error\":\"" + ExceptionMessages.INVALID_JWT_TOKEN + "\"}", stringWriter.toString());
		verify(filterChain, never()).doFilter(request, response);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}
