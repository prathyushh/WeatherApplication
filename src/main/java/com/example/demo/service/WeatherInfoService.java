package com.example.demo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherInfoService {

	private final CityRepository cityRepository;
	private final WeatherProvider weatherProvider;
	private final AuditService auditService;

	public WeatherInfoService(CityRepository cityRepository, WeatherProvider weatherProvider,
			AuditService auditService) {

		this.cityRepository = cityRepository;
		this.weatherProvider = weatherProvider;
		this.auditService = auditService;
	}

	public Page<City> getCities(Pageable pageable) {
		return cityRepository.findAll(pageable);
	}

	public WeatherResponse getWeather(String city, String state) {

		try {
			City cityEntity = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(city, state)
					.orElseThrow(() -> new CityNotFoundException("City not found!"));

			WeatherResponse response = weatherProvider.getWeather(cityEntity);

			auditService.logAction(getCurrentUsername(), "GET_WEATHER",
					"Weather requested for: " + city + ", " + state);

			log.info("Weather retrieved successfully: {}, {}", city, state);

			return response;

		} catch (Exception ex) {

			log.error("Failed to retrieve weather for: {}, {}", city, state, ex);

			throw ex;
		}
	}

	private String getCurrentUsername() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}
}