package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

	@Mock
	private WeatherService weatherInfoService;

	@InjectMocks
	private WeatherController weatherController;

	@Test
	void getWeather_shouldReturnWeather() {

		String city = "Kota";
		String state = "Rajasthan";

		WeatherResponse weatherResponse = new WeatherResponse();

		when(weatherInfoService.getWeather(city, state)).thenReturn(weatherResponse);

		ResponseEntity<WeatherResponse> response = weatherController.getWeather(city, state);

		assertNotNull(response);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		assertEquals(weatherResponse, response.getBody());

		verify(weatherInfoService).getWeather(city, state);
	}
}