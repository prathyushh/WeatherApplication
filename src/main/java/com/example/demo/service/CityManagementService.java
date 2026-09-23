package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.GeocodingLocation;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CityManagementService {

	private final LocationProvider locationProvider;
	private final CityRepository cityRepository;
	private final AuditService auditService;

	public CityManagementService(LocationProvider locationProvider, AuditService auditService,
			CityRepository cityRepository) {

		this.locationProvider = locationProvider;
		this.cityRepository = cityRepository;
		this.auditService = auditService;
	}

	@Transactional
	public City addCity(CityRequest cityRequest) {

		try {
			Optional<City> existingCity = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(cityRequest.getCity(),
					cityRequest.getState());

			if (existingCity.isPresent()) {
				throw new CityAlreadyExistsException("City already exists!");
			}

			GeocodingLocation location = locationProvider.getLocation(cityRequest.getCity(), cityRequest.getState(),
					cityRequest.getCountryCode());

			City savedCity = City.builder().city(location.getCity()).countryCode(location.getCountry())
					.state(location.getState()).longitude(location.getLon()).latitude(location.getLat()).build();

			cityRepository.save(savedCity);

			auditService.logAction(getCurrentUsername(), "ADD_CITY", "City added: " + savedCity.getCity() + ", "
					+ savedCity.getState() + ", " + savedCity.getCountryCode());

			log.info("City added successfully: {}, {}", savedCity.getCity(), savedCity.getState());

			return savedCity;

		} catch (Exception ex) {

			log.error("Failed to add city: {}, {}", cityRequest.getCity(), cityRequest.getState(), ex);

			throw ex;
		}
	}

	public Page<City> getCities(Pageable pageable) {
		return cityRepository.findAll(pageable);
	}

	@Transactional
	public void deleteCity(Long id) {

		try {
			City city = cityRepository.findById(id)
					.orElseThrow(() -> new CityNotFoundException("No City exists for corresponding Id"));

			cityRepository.delete(city);

			auditService.logAction(getCurrentUsername(), "DELETE_CITY", "City deleted: " + city.getCity());

			log.info("City deleted successfully: {}, {}", city.getCity(), city.getState());

		} catch (Exception ex) {

			log.error("Failed to delete city with id: {}", id, ex);

			throw ex;
		}
	}

	private String getCurrentUsername() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getName();
	}
}