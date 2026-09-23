package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CityRequest;
import com.example.demo.entity.City;
import com.example.demo.service.CityManagementService;

import jakarta.validation.Valid;

@RestController
public class AdminController {
	private final CityManagementService cityManagementService;

	public AdminController(CityManagementService cityManagementService) {
		this.cityManagementService = cityManagementService;

	}

	@PostMapping("/addCity")
	public ResponseEntity<City> addCity(@Valid @RequestBody CityRequest cityRequest) {
		return ResponseEntity.ok(cityManagementService.addCity(cityRequest));
	}

	@DeleteMapping("/deleteCity/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id) {
		cityManagementService.deleteCity(id);
		return ResponseEntity.ok("City deleted");
	}

}
