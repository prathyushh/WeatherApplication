
package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.GeocodingApiResponse;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityPersistenceServiceTest {
	@Mock
	private CityRepository cityRepository;
	@Mock
	private AuditService auditService;
	@Mock
	private Authentication authentication;
	@InjectMocks
	private CityPersistenceService cityPersistenceService;

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void saveCityWithAudit_shouldSaveCityAndRecordAudit() {
		GeocodingApiResponse location = new GeocodingApiResponse();
		location.setCity("Kota");
		location.setState("Rajasthan");
		location.setCountry("IN");
		location.setLat(25.2138);
		location.setLon(75.8648);
		when(authentication.getName()).thenReturn("admin");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		when(cityRepository.save(any(City.class))).thenAnswer(invocation -> invocation.getArgument(0));
		City result = cityPersistenceService.saveCityWithAudit(location);
		assertNotNull(result);
		assertEquals("Kota", result.getCity());
		assertEquals("Rajasthan", result.getState());
		assertEquals("IN", result.getCountryCode());
		assertEquals(25.2138, result.getLatitude());
		assertEquals(75.8648, result.getLongitude());
		verify(cityRepository).save(any(City.class));
		verify(auditService).recordAudit("admin", "ADD_CITY", "City added: Kota, Rajasthan, IN");
	}
}
