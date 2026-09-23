package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CityRequest {
	@NotBlank
	private String city;
	@NotBlank
	private String state;
	@NotBlank
	private String countryCode;

}
