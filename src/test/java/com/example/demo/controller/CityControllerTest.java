package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.CityResponse;
import com.example.demo.entity.City;
import com.example.demo.service.CityService;

@ExtendWith(MockitoExtension.class)
class CityControllerTest {

	@Mock
	private CityService cityService;

	@InjectMocks
	private CityController cityController;

	@Test
	void addCity_shouldReturnCreatedCity() {

		CityRequest cityRequest = new CityRequest();

		City city = new City();

		when(cityService.createCity(cityRequest)).thenReturn(city);

		ResponseEntity<City> response = cityController.addCity(cityRequest);

		assertNotNull(response);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

		assertEquals(city, response.getBody());

		verify(cityService).createCity(cityRequest);
	}

	@Test
	void getCities_shouldReturnCities() {

		Pageable pageable = PageRequest.of(0, 10);

		CityResponse cityResponse = new CityResponse();

		Page<CityResponse> cityPage = new PageImpl<>(List.of(cityResponse), pageable, 1);

		when(cityService.getCities(pageable)).thenReturn(cityPage);

		ResponseEntity<Page<CityResponse>> response = cityController.getCities(pageable);

		assertNotNull(response);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		assertEquals(cityPage, response.getBody());

		assertNotNull(response.getBody());

		assertEquals(1, response.getBody().getTotalElements());

		verify(cityService).getCities(pageable);
	}

	@Test
	void deleteCity_shouldDeleteCity() {

		Long cityId = 1L;

		ResponseEntity<String> response = cityController.deleteCity(cityId);

		assertNotNull(response);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		assertEquals("City deleted", response.getBody());

		verify(cityService).deleteCity(cityId);
	}
}