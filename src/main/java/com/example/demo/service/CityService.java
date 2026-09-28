package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.CityResponse;
import com.example.demo.dto.GeocodingApiResponse;
import com.example.demo.dto.StringResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CityService {
	private final LocationProvider locationProvider;
	private final CityRepository cityRepository;
	private final AuditService auditService;
	private final WeatherProviderService weatherProviderService;
	private final CityTransactionService cityTransactionService;

	public City createCity(CityRequest cityRequest) {
		try {
			Optional<City> existingCity = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(cityRequest.getCity(),
					cityRequest.getState());
			if (existingCity.isPresent()) {
				throw new CityAlreadyExistsException("City already exists!");
			}
			GeocodingApiResponse location = locationProvider.findLocation(cityRequest.getCity(), cityRequest.getState(),
					cityRequest.getCountryCode());
			City savedCity = cityTransactionService.saveCityWithAudit(location);
			log.info("City added successfully: {}, {}", savedCity.getCity(), savedCity.getState());
			return savedCity;
		} catch (Exception ex) {
			log.error("Failed to add city: {}, {}:{}", cityRequest.getCity(), cityRequest.getState(), ex.getMessage());
			throw ex;
		}
	}

	public Page<CityResponse> getCities(Pageable pageable) {
		Page<City> cities = cityRepository.findAll(pageable);
		return cities.map(city -> new CityResponse(city.getId(), city.getCity(), city.getState(), city.getCountryCode(),
				city.getLatitude(), city.getLongitude()));
	}

	@Transactional
	public StringResponse deleteCity(Long id) {
		try {
			City city = cityRepository.findById(id)
					.orElseThrow(() -> new CityNotFoundException("No City exists for corresponding Id"));
			weatherProviderService.deleteWeatherCache(city);
			cityRepository.delete(city);
			auditService.recordAudit(getCurrentUsername(), "DELETE_CITY", "City deleted: " + city.getCity());
			log.info("City deleted successfully: {}, {}", city.getCity(), city.getState());
			return new StringResponse("City deleted successfully");
		} catch (Exception ex) {
			log.error("Failed to delete city with id: {} :{}", id, ex.getMessage());
			throw ex;
		}
	}

	private String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}
}