package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.config.OpenWeatherApiUriBuilder;
import com.example.demo.constant.ExceptionMessages;
import com.example.demo.dto.WeatherApiResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.WeatherApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherProviderService implements WeatherProvider {
	private final RestClient restClient;
	@Value("${weather.api.key}")
	private String apiKey;

	@Override
	@Cacheable(value = "weather", key = "#city.latitude + '_' + #city.longitude")
	public WeatherResponse getWeather(City city) {
		log.atInfo().log("CACHE MISS - Calling OpenWeather API for {}, {}", city.getLatitude(), city.getLongitude());
		try {
			WeatherApiResponse response = restClient.get()
					.uri(uriBuilder -> OpenWeatherApiUriBuilder
							.weather(uriBuilder, city.getLatitude(), city.getLongitude(), apiKey).build())
					.retrieve().body(WeatherApiResponse.class);
			return new WeatherResponse(city.getCity(), city.getState(), city.getCountryCode(),
					response.getMain().getTemp(), response.getMain().getFeels_like(), response.getMain().getHumidity(),
					response.getWeather().get(0).getMain(), response.getWeather().get(0).getDescription(),
					response.getWind().getSpeed());
		} catch (Exception ex) {
			log.error("OpenWeather API failed for {}, {} : {}", city.getLatitude(), city.getLongitude(), ex.getMessage());
			throw new WeatherApiException(ExceptionMessages.API_WEATHER_ERROR);
		}
	}

	@CacheEvict(value = "weather", key = "#city.latitude + '_' + #city.longitude")
	public void deleteWeatherCache(City city) {
		log.info("Weather cache deleted for {}, {}", city.getLatitude(), city.getLongitude());
	}
}
