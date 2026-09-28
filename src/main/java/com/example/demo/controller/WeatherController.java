package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
	private final WeatherService weatherInfoService;

	@GetMapping
	public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city, @RequestParam String state) {
		return ResponseEntity.ok(weatherInfoService.getWeather(city, state));
	}
}
