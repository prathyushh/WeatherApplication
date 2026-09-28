package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
	@Mock
	private UserRepository repository;
	@InjectMocks
	private CustomUserDetailsService userDetailsService;

	@Test
	void loadUserByUsername_shouldReturnUserDetails() {
		User user = new User();
		user.setUsername("admin");
		user.setPassword("encodedPassword");
		user.setRole(Role.ADMIN);
		when(repository.findByUsername("admin")).thenReturn(Optional.of(user));
		UserDetails result = userDetailsService.loadUserByUsername("admin");
		assertNotNull(result);
		assertEquals("admin", result.getUsername());
		assertEquals("encodedPassword", result.getPassword());
		assertTrue(
				result.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
		verify(repository).findByUsername("admin");
	}

	@Test
	void loadUserByUsername_shouldThrowException_whenUserNotFound() {
		when(repository.findByUsername("unknown")).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userDetailsService.loadUserByUsername("unknown"));
		assertEquals("user not found", exception.getMessage());
		verify(repository).findByUsername("unknown");
	}
}