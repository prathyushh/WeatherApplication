package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityResponse {
	private Long id;
	private String city;
	private String state;
	private String countryCode;
	private double latitude;
	private double longitude;

}
