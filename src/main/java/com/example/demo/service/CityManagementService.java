package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.GeocodingLocation;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

@Service
public class CityManagementService {
	private final LocationProvider locationProvider;
	private final CityRepository cityRepository;

	public CityManagementService(LocationProvider locationProvider, CityRepository cityRepository) {
		this.locationProvider = locationProvider;
		this.cityRepository = cityRepository;
	}

	public City addCity(CityRequest cityRequest) {
		GeocodingLocation location = locationProvider.getLocation(cityRequest.getCity(), cityRequest.getState(),
				cityRequest.getCountryCode());
		City city = City.builder().city(location.getCity()).countryCode(location.getCountry())
				.state(location.getState()).longitude(location.getLon()).latitude(location.getLat()).build();
		return cityRepository.save(city);

	}

}
