package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {
	private final CityRepository cityRepository;
	private final WeatherProvider weatherProvider;
	private final AuditService auditService;

	public WeatherResponse getWeather(String city, String state) {
		try {
			City cityEntity = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)
					.orElseThrow(() -> new CityNotFoundException("City not found!"));
			WeatherResponse response = weatherProvider.getWeather(cityEntity);
			auditService.recordAudit(getCurrentUsername(), "GET_WEATHER",
					"Weather requested for: " + city + ", " + state);
			log.info("Weather retrieved successfully: {}, {}", city, state);
			return response;
		} catch (Exception ex) {
			log.error("Failed to retrieve weather for: {}, {} : {}", city, state, ex.getMessage());
			throw ex;
		}
	}

	private String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}