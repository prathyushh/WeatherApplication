package com.example.demo.dto;

import com.example.demo.constant.ValidationMessages;
import com.example.demo.enums.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
	@NotBlank
	private String username;
	@Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = ValidationMessages.PASSWORD_INVALID)
	private String password;
	private Role role;
}
