package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.CityResponse;
import com.example.demo.dto.GeocodingApiResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

	@Mock
	private LocationProvider locationProvider;

	@Mock
	private CityRepository cityRepository;

	@Mock
	private AuditService auditService;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private CityService cityService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void createCity_shouldCreateCity() {

		CityRequest request = new CityRequest();

		request.setCity("Kota");
		request.setState("Rajasthan");
		request.setCountryCode("IN");

		GeocodingApiResponse location = new GeocodingApiResponse();

		location.setCity("Kota");
		location.setState("Rajasthan");
		location.setCountry("IN");
		location.setLat(25.2138);
		location.setLon(75.8648);

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan")).thenReturn(Optional.empty());

		when(locationProvider.findLocation("Kota", "Rajasthan", "IN")).thenReturn(location);

		when(authentication.getName()).thenReturn("admin");

		SecurityContextHolder.getContext().setAuthentication(authentication);

		City result = cityService.createCity(request);

		assertNotNull(result);

		assertEquals("Kota", result.getCity());

		assertEquals("Rajasthan", result.getState());

		assertEquals("IN", result.getCountryCode());

		assertEquals(25.2138, result.getLatitude());

		assertEquals(75.8648, result.getLongitude());

		verify(cityRepository).save(any(City.class));

		verify(auditService).recordAudit("admin", "ADD_CITY", "City added: Kota, Rajasthan, IN");
	}

	@Test
	void createCity_shouldThrowException_whenCityAlreadyExists() {

		CityRequest request = new CityRequest();

		request.setCity("Kota");
		request.setState("Rajasthan");
		request.setCountryCode("IN");

		City existingCity = new City();

		existingCity.setCity("Kota");
		existingCity.setState("Rajasthan");

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan"))
				.thenReturn(Optional.of(existingCity));

		assertThrows(CityAlreadyExistsException.class, () -> cityService.createCity(request));

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan");

		verifyNoInteractions(locationProvider);

		verifyNoInteractions(auditService);
	}

	@Test
	void getCities_shouldReturnCities() {

		Pageable pageable = PageRequest.of(0, 10);

		City city = City.builder().id(1L).city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();

		Page<City> cityPage = new PageImpl<>(List.of(city), pageable, 1);

		when(cityRepository.findAll(pageable)).thenReturn(cityPage);

		Page<CityResponse> result = cityService.getCities(pageable);

		assertNotNull(result);

		assertEquals(1, result.getTotalElements());

		assertEquals(1, result.getContent().size());

		CityResponse response = result.getContent().get(0);

		assertEquals(1L, response.getId());

		assertEquals("Kota", response.getCity());

		assertEquals("Rajasthan", response.getState());

		assertEquals("IN", response.getCountryCode());

		assertEquals(25.2138, response.getLatitude());

		assertEquals(75.8648, response.getLongitude());

		verify(cityRepository).findAll(pageable);
	}

	@Test
	void deleteCity_shouldDeleteCity() {

		Long cityId = 1L;

		City city = City.builder().id(cityId).city("Kota").state("Rajasthan").countryCode("IN").build();

		when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

		when(authentication.getName()).thenReturn("admin");

		SecurityContextHolder.getContext().setAuthentication(authentication);

		cityService.deleteCity(cityId);

		verify(cityRepository).findById(cityId);

		verify(cityRepository).delete(city);

		verify(auditService).recordAudit("admin", "DELETE_CITY", "City deleted: Kota");
	}

	@Test
	void deleteCity_shouldThrowException_whenCityNotFound() {

		Long cityId = 1L;

		when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

		assertThrows(CityNotFoundException.class, () -> cityService.deleteCity(cityId));

		verify(cityRepository).findById(cityId);

		verifyNoInteractions(auditService);
	}
}