package com.example.demo.service;

import com.example.demo.dto.WeatherResponse;

public interface WeatherProvider {
       WeatherResponse getWeather(String city,String state);
}
