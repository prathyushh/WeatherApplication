package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.WeatherApiResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.exception.WeatherApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherProviderService implements WeatherProvider {
	private final RestClient restClient;
	@Value("${weather.api.key}")
	private String apiKey;

	public WeatherProviderService(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	@Cacheable(value = "weather", key = "#city.latitude + '_' + #city.longitude")
	public WeatherResponse getWeather(City city) {

		log.info("CACHE MISS - Calling OpenWeather API for {}, {}", city.getLatitude(), city.getLongitude());

		try {
			WeatherApiResponse response = restClient.get()
					.uri(uriBuilder -> uriBuilder.path("/data/2.5/weather").queryParam("lat", city.getLatitude())
							.queryParam("lon", city.getLongitude()).queryParam("appid", apiKey)
							.queryParam("units", "metric").build())
					.retrieve().body(WeatherApiResponse.class);

			return new WeatherResponse(city.getCity(), city.getState(), city.getCountryCode(),
					response.getMain().getTemp(), response.getMain().getFeels_like(), response.getMain().getHumidity(),
					response.getWeather().get(0).getMain(), response.getWeather().get(0).getDescription(),
					response.getWind().getSpeed());

		} catch (Exception ex) {
			log.error("OpenWeather API failed for {}, {}", city.getLatitude(), city.getLongitude(), ex);

			throw new WeatherApiException("Unable to retrieve weather from OpenWeather");
		}
	}

}
