package com.example.demo.service;

import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;

public interface WeatherProvider {
	WeatherResponse getWeather(City city);
}
