package com.example.demo.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.service.WeatherInfoService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final WeatherInfoService weatherInfoService;

	public UserController(WeatherInfoService weatherInfoService) {
		this.weatherInfoService = weatherInfoService;
	}

	@GetMapping("/getCities")
	public ResponseEntity<Page<City>> getCities(Pageable pageable) {
		return ResponseEntity.ok(weatherInfoService.getCities(pageable));

	}

	@GetMapping("/getWeather")
	public ResponseEntity<WeatherResponse> getWeather(String city, String state) {
		return ResponseEntity.ok(weatherInfoService.getWeather(city, state));
	}

}
