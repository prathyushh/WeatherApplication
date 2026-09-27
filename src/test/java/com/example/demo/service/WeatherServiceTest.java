package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private WeatherProvider weatherProvider;

	@Mock
	private AuditService auditService;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private WeatherService weatherService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void getWeather_shouldReturnWeather() {

		String city = "Kota";
		String state = "Rajasthan";

		City cityEntity = City.builder().id(1L).city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();

		WeatherResponse weatherResponse = new WeatherResponse();

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)).thenReturn(Optional.of(cityEntity));

		when(weatherProvider.getWeather(cityEntity)).thenReturn(weatherResponse);

		when(authentication.getName()).thenReturn("admin");

		SecurityContextHolder.getContext().setAuthentication(authentication);

		WeatherResponse result = weatherService.getWeather(city, state);

		assertNotNull(result);

		assertEquals(weatherResponse, result);

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase(city, state);

		verify(weatherProvider).getWeather(cityEntity);

		verify(auditService).recordAudit("admin", "GET_WEATHER", "Weather requested for: Kota, Rajasthan");
	}

	@Test
	void getWeather_shouldThrowException_whenCityNotFound() {

		String city = "Unknown";
		String state = "Rajasthan";

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)).thenReturn(Optional.empty());

		CityNotFoundException exception = assertThrows(CityNotFoundException.class,
				() -> weatherService.getWeather(city, state));

		assertEquals("City not found!", exception.getMessage());

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase(city, state);

		verifyNoInteractions(weatherProvider);
		verifyNoInteractions(auditService);
	}

	@Test
	void getWeather_shouldThrowException_whenWeatherProviderFails() {

		String city = "Kota";
		String state = "Rajasthan";

		City cityEntity = City.builder().id(1L).city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)).thenReturn(Optional.of(cityEntity));

		when(weatherProvider.getWeather(cityEntity)).thenThrow(new RuntimeException("Weather API failed"));

		assertThrows(RuntimeException.class, () -> weatherService.getWeather(city, state));

		verify(weatherProvider).getWeather(cityEntity);

		verifyNoInteractions(auditService);
	}
}