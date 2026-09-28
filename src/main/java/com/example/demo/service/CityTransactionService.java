package com.example.demo.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.GeocodingApiResponse;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CityTransactionService {
	private final CityRepository cityRepository;
	private final AuditService auditService;

	@Transactional
	public City saveCityWithAudit(GeocodingApiResponse location) {
		City savedCity = City.builder().city(location.getCity()).countryCode(location.getCountry())
				.state(location.getState()).longitude(location.getLon()).latitude(location.getLat()).build();
		cityRepository.save(savedCity);
		auditService.recordAudit(getCurrentUsername(), "ADD_CITY",
				"City added: " + savedCity.getCity() + ", " + savedCity.getState() + ", " + savedCity.getCountryCode());
		return savedCity;
	}

	private String getCurrentUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}