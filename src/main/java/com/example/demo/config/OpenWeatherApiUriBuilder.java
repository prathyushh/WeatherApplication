package com.example.demo.config;

import org.springframework.web.util.UriBuilder;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class OpenWeatherApiUriBuilder {
	public static UriBuilder weather(UriBuilder uriBuilder, double latitude, double longitude, String apiKey) {
		return uriBuilder.path("/data/2.5/weather").queryParam("lat", latitude).queryParam("lon", longitude)
				.queryParam("appid", apiKey).queryParam("units", "metric");
	}

	public static UriBuilder geocoding(UriBuilder uriBuilder, String city, String country, String apiKey) {
		return uriBuilder.path("/geo/1.0/direct").queryParam("q", city + "," + country).queryParam("limit", 5)
				.queryParam("appid", apiKey);
	}
}