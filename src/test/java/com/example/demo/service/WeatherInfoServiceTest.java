package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class WeatherInfoServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private WeatherProvider weatherProvider;

	@Mock
	private AuditService auditService;

	@InjectMocks
	private WeatherInfoService weatherInfoService;

	@BeforeEach
	void setUp() {

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));
	}

	@AfterEach
	void tearDown() {

		SecurityContextHolder.clearContext();
	}

	@Test
	void getCities_shouldReturnCities() {

		Pageable pageable = PageRequest.of(0, 10);

		City city = City.builder().city("Kota").state("Rajasthan").countryCode("IN").build();

		Page<City> page = new PageImpl<>(java.util.List.of(city), pageable, 1);

		when(cityRepository.findAll(pageable)).thenReturn(page);

		Page<City> result = weatherInfoService.getCities(pageable);

		assertEquals(1, result.getTotalElements());
		assertEquals("Kota", result.getContent().get(0).getCity());

		verify(cityRepository).findAll(pageable);
	}

	@Test
	void getWeather_shouldReturnWeather_whenCityExists() {

		City city = City.builder().city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan")).thenReturn(Optional.of(city));

		WeatherResponse weatherResponse = new WeatherResponse("Kota", "Rajasthan", "IN", 30.5, 32.0, 60, "Clouds",
				"scattered clouds", 4.5);

		when(weatherProvider.getWeather(city)).thenReturn(weatherResponse);

		WeatherResponse result = weatherInfoService.getWeather("Kota", "Rajasthan");

		assertEquals("Kota", result.getCity());
		assertEquals("Rajasthan", result.getState());
		assertEquals("IN", result.getCountryCode());
		assertEquals(30.5, result.getTemperature());

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan");

		verify(weatherProvider).getWeather(city);

		verify(auditService).logAction(eq("admin"), eq("GET_WEATHER"), eq("Weather requested for: Kota, Rajasthan"));
	}

	@Test
	void getWeather_shouldThrowException_whenCityDoesNotExist() {

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan")).thenReturn(Optional.empty());

		CityNotFoundException exception = assertThrows(CityNotFoundException.class,
				() -> weatherInfoService.getWeather("Kota", "Rajasthan"));

		assertEquals("City not found!", exception.getMessage());

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan");

		verify(weatherProvider, never()).getWeather(any(City.class));

		verify(auditService, never()).logAction(any(), any(), any());
	}
}