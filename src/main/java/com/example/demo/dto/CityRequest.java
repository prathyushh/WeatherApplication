package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CityRequest {

	@NotBlank(message = "City is required")
	@Pattern(regexp = "^[a-zA-Z\\s.-]+$", message = "City must contain only letters, spaces, dots or hyphens")
	private String city;

	@NotBlank(message = "State is required")
	@Pattern(regexp = "^[a-zA-Z\\s.-]+$", message = "State must contain only letters, spaces, dots or hyphens")
	private String state;

	@NotBlank(message = "Country code is required")
	@Pattern(regexp = "^[A-Za-z]{2}$", message = "Country code must be a 2-letter country code")
	private String countryCode;
}