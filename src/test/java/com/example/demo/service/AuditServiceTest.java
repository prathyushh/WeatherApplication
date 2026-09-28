package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Audit;
import com.example.demo.entity.User;
import com.example.demo.repository.AuditRepository;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {
	@Mock
	private AuditRepository auditRepository;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private AuditService auditService;

	@Test
	void recordAudit_shouldSaveAudit() {
		String username = "admin";
		String action = "ADD_CITY";
		String details = "Added Kota";
		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		auditService.recordAudit(username, action, details);
		verify(userRepository).findByUsername(username);
		verify(auditRepository).save(any(Audit.class));
	}

	@Test
	void recordAudit_shouldThrowException_whenUserNotFound() {
		String username = "unknown";
		String action = "ADD_CITY";
		String details = "Added Kota";
		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> auditService.recordAudit(username, action, details));
		assertEquals("User not found", exception.getMessage());
		verify(userRepository).findByUsername(username);
	}
}