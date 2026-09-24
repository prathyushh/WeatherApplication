package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.GeocodingLocation;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityManagementServiceTest {

	@Mock
	private LocationProvider locationProvider;

	@Mock
	private CityRepository cityRepository;

	@Mock
	private AuditService auditService;

	@InjectMocks
	private CityManagementService cityManagementService;

	@BeforeEach
	void setUp() {

		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("admin", null));
	}

	@AfterEach
	void tearDown() {

		SecurityContextHolder.clearContext();
	}

	@Test
	void addCity_shouldSaveCity_whenCityDoesNotExist() {

		CityRequest cityRequest = new CityRequest();

		cityRequest.setCity("Kota");
		cityRequest.setState("Rajasthan");
		cityRequest.setCountryCode("IN");

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan")).thenReturn(Optional.empty());

		GeocodingLocation location = new GeocodingLocation();

		location.setCity("Kota");
		location.setState("Rajasthan");
		location.setCountry("IN");
		location.setLat(25.2138);
		location.setLon(75.8648);

		when(locationProvider.getLocation("Kota", "Rajasthan", "IN")).thenReturn(location);

		City result = cityManagementService.addCity(cityRequest);

		assertEquals("Kota", result.getCity());
		assertEquals("Rajasthan", result.getState());
		assertEquals("IN", result.getCountryCode());
		assertEquals(25.2138, result.getLatitude());
		assertEquals(75.8648, result.getLongitude());

		verify(cityRepository).findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan");

		verify(locationProvider).getLocation("Kota", "Rajasthan", "IN");

		verify(cityRepository).save(any(City.class));

		verify(auditService).logAction(eq("admin"), eq("ADD_CITY"), eq("City added: Kota, Rajasthan, IN"));
	}

	@Test
	void addCity_shouldThrowException_whenCityAlreadyExists() {

		CityRequest cityRequest = new CityRequest();

		cityRequest.setCity("Kota");
		cityRequest.setState("Rajasthan");
		cityRequest.setCountryCode("IN");

		City existingCity = City.builder().city("Kota").state("Rajasthan").countryCode("IN").build();

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Kota", "Rajasthan"))
				.thenReturn(Optional.of(existingCity));

		CityAlreadyExistException exception = assertThrows(CityAlreadyExistException.class,
				() -> cityManagementService.addCity(cityRequest));

		assertEquals("City already exists!", exception.getMessage());

		verify(locationProvider, never()).getLocation(any(), any(), any());

		verify(cityRepository, never()).save(any(City.class));

		verify(auditService, never()).logAction(any(), any(), any());
	}

	@Test
	void getCities_shouldReturnCitiesFromRepository() {

		Pageable pageable = PageRequest.of(0, 10);

		City city1 = City.builder().city("Kota").state("Rajasthan").countryCode("IN").build();

		City city2 = City.builder().city("Jaipur").state("Rajasthan").countryCode("IN").build();

		Page<City> expectedPage = new PageImpl<>(java.util.List.of(city1, city2), pageable, 2);

		when(cityRepository.findAll(pageable)).thenReturn(expectedPage);

		Page<City> result = cityManagementService.getCities(pageable);

		assertSame(expectedPage, result);

		assertEquals(2, result.getTotalElements());

		verify(cityRepository).findAll(pageable);
	}

	@Test
	void deleteCity_shouldDeleteCity_whenCityExists() {

		Long cityId = 1L;

		City city = City.builder().city("Kota").state("Rajasthan").countryCode("IN").build();

		when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

		cityManagementService.deleteCity(cityId);

		verify(cityRepository).findById(cityId);

		verify(cityRepository).delete(city);

		verify(auditService).logAction(eq("admin"), eq("DELETE_CITY"), eq("City deleted: Kota"));
	}

	@Test
	void deleteCity_shouldThrowException_whenCityDoesNotExist() {

		Long cityId = 100L;

		when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

		CityNotFoundException exception = assertThrows(CityNotFoundException.class,
				() -> cityManagementService.deleteCity(cityId));

		assertEquals("No City exists for corresponding Id", exception.getMessage());

		verify(cityRepository).findById(cityId);

		verify(cityRepository, never()).delete(any(City.class));

		verify(auditService, never()).logAction(any(), any(), any());
	}
}