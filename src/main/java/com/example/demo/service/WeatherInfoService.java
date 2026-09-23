package com.example.demo.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

@Service
public class WeatherInfoService {
	private final CityRepository cityRepository;
	private final WeatherProvider weatherProvider;
	public WeatherInfoService(CityRepository cityRepository,WeatherProvider weatherProvider) {
		this.cityRepository=cityRepository;
		this.weatherProvider=weatherProvider;
	}

	public Page<City> getCities(Pageable pageable) {

		return cityRepository.findAll(pageable);
	}
	
	public WeatherResponse getWeather(String city,String state) {
		return weatherProvider.getWeather(city, state);
	}

	
	

}
