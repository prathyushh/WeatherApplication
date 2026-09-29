package com.example.demo.dto;

import com.example.demo.constant.ValidationMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CityRequest {
	@NotBlank(message = ValidationMessages.CITY_REQUIRED)
	@Pattern(regexp = "^[a-zA-Z\\s.-]+$", message = ValidationMessages.CITY_INVALID)
	private String city;
	@NotBlank(message = ValidationMessages.STATE_REQUIRED)
	@Pattern(regexp = "^[a-zA-Z\\s.-]+$", message = ValidationMessages.STATE_INVALID)
	private String state;
	@NotBlank(message = ValidationMessages.COUNTRY_CODE_REQUIRED)
	@Pattern(regexp = "^[A-Za-z]{2}$", message = ValidationMessages.COUNTRY_CODE_INVALID)
	private String countryCode;
}