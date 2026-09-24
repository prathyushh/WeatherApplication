package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Audit;
import com.example.demo.entity.User;
import com.example.demo.repository.AuditRepository;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuditService {

	private final AuditRepository auditRepository;
	private final UserRepository userRepository;

	public AuditService(AuditRepository auditRepository, UserRepository userRepository) {
		this.auditRepository = auditRepository;
		this.userRepository = userRepository;
	}

	public void logAction(String username, String action, String details) {

		try {
			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new RuntimeException("User not found"));

			Audit audit = new Audit();

			audit.setUserId(user.getId());
			audit.setAction(action);
			audit.setTimestamp(LocalDateTime.now());
			audit.setDetails(details);

			auditRepository.save(audit);

			log.info("Audit saved successfully - user: {}, action: {}", username, action);

		} catch (Exception ex) {

			log.error("Failed to save audit - user: {}, action: {}", username, action, ex.getMessage());

			throw ex;
		}
	}
}