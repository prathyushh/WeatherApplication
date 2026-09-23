package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CityRequest;
import com.example.demo.dto.GeocodingLocation;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
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
		Optional<City> existingCity = cityRepository.findByCityAndState(cityRequest.getCity(), cityRequest.getState());
		if (existingCity.isPresent()) {
			throw new CityAlreadyExistsException("City already exists!");
		}
		GeocodingLocation location = locationProvider.getLocation(cityRequest.getCity(), cityRequest.getState(),
				cityRequest.getCountryCode());
		City city = City.builder().city(location.getCity()).countryCode(location.getCountry())
				.state(location.getState()).longitude(location.getLon()).latitude(location.getLat()).build();
		return cityRepository.save(city);

	}

	public Page<City> getCities(Pageable pageable) {

		return cityRepository.findAll(pageable);
	}

	public void deleteCity(Long id) {
		City city = cityRepository.findById(id)
				.orElseThrow(() -> new CityNotFoundException("No City exists for corresponding Id"));
		cityRepository.delete(city);

	}

}
