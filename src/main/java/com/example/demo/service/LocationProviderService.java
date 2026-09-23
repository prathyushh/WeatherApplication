package com.example.demo.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.GeocodingLocation;
import com.example.demo.exception.LocationApiException;

@Service
public class LocationProviderService implements LocationProvider {
	private final RestClient restClient;

	public LocationProviderService(RestClient restClient) {
		this.restClient = restClient;
	}

	@Value("${weather.api.key}")
	private String apiKey;

	@Override
	public GeocodingLocation getLocation(String city, String state, String country) {
		try {
			GeocodingLocation[] locations = restClient.get()

					.uri(uriBuilder -> uriBuilder.path("geo/1.0/direct").queryParam("q", city + "," + country)
							.queryParam("limit", 5).queryParam("appId", apiKey).build())
					.retrieve().body(GeocodingLocation[].class);
			return Arrays.stream(locations).filter(location -> location.getCity().equalsIgnoreCase(city)
					&& location.getCountry().equalsIgnoreCase(country) && location.getState().equalsIgnoreCase(state))
					.findFirst().orElseThrow(() -> new LocationApiException("city and state combination doesnt match"));
		} catch (LocationApiException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new LocationApiException("Unable to retrieve location from OpenWeather");
		}

	}

}
