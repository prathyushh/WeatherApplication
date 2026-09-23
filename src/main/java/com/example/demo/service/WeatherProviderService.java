package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.WeatherApiResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

@Service
public class WeatherProviderService implements WeatherProvider {
	private final RestClient restClient;
	private final CityRepository cityRepository;
	@Value("${weather.api.key}")
	private String apiKey;

	public WeatherProviderService(RestClient restClient, CityRepository cityRepository) {
		this.restClient = restClient;
		this.cityRepository = cityRepository;
	}

	@Override
	public WeatherResponse getWeather(String cityname, String state) {
		City city = cityRepository.findByCityAndState(cityname, state);
		WeatherApiResponse response = restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/data/2.5/weather").queryParam("lat", city.getLatitude())
						.queryParam("lon", city.getLongitude()).queryParam("appid", apiKey)
						.queryParam("units", "metric").build())
				.retrieve().body(WeatherApiResponse.class);
		return new WeatherResponse(city.getCity(), city.getState(), city.getCountryCode(), response.getMain().getTemp(),
				response.getMain().getFeels_like(), response.getMain().getHumidity(),
				response.getWeather().get(0).getMain(), response.getWeather().get(0).getDescription(),
				response.getWind().getSpeed());

	}

}
