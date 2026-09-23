package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CityRequest;
import com.example.demo.entity.City;
import com.example.demo.service.CityManagementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private final CityManagementService cityManagementService;
	public AdminController(CityManagementService cityManagementService) {
		this.cityManagementService=cityManagementService;
	}
	@PostMapping
	public ResponseEntity<City> addCity(@Valid @RequestBody CityRequest cityRequest){
		return ResponseEntity.ok(cityManagementService.addCity(cityRequest));
	}

}
