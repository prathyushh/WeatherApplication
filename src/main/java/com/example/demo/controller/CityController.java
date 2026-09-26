package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.CityResponse;
import com.example.demo.entity.City;
import com.example.demo.service.CityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cities")
public class CityController {
	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;

	}

	@PostMapping
	public ResponseEntity<City> addCity(@Valid @RequestBody CityRequest cityRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cityService.createCity(cityRequest));
	}

	@GetMapping
	public ResponseEntity<Page<CityResponse>> getCities(Pageable pageable) {
		return ResponseEntity.ok(cityService.getCities(pageable));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id) {
		cityService.deleteCity(id);
		return ResponseEntity.ok("City deleted");
	}

}
