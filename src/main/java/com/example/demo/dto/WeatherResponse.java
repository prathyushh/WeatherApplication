package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {
	private String city;
	private String state;
	private String countryCode;
	private Double temperature;
	private Double feelsLike;
	private Integer humidity;
	private String main;
	private String description;
	private Double windSpeed;
}
