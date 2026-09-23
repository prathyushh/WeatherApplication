package com.example.demo.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CityRequest;
import com.example.demo.entity.City;
import com.example.demo.service.CityManagementService;
import com.example.demo.service.WeatherInfoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private final CityManagementService cityManagementService;
	private final WeatherInfoService weatherInfoService;
	public AdminController(CityManagementService cityManagementService,WeatherInfoService weatherInfoService) {
		this.cityManagementService=cityManagementService;
		this.weatherInfoService=weatherInfoService;
	}
	@PostMapping
	public ResponseEntity<City> addCity(@Valid @RequestBody CityRequest cityRequest){
		return ResponseEntity.ok(cityManagementService.addCity(cityRequest));
	}
	@GetMapping
	public ResponseEntity<Page<City>> getCities(Pageable pageable) {
		return ResponseEntity.ok(weatherInfoService.getCities(pageable));

	}
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id){
		cityManagementService.deleteCity(id);
		return ResponseEntity.ok("City deleted");
	}

}
