package com.example.demo.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.GeocodingLocation;
import com.example.demo.exception.LocationApiException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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

			GeocodingLocation location = Arrays.stream(locations)
					.filter(loc -> loc.getCity().equalsIgnoreCase(city) && loc.getCountry().equalsIgnoreCase(country)
							&& loc.getState().equalsIgnoreCase(state))
					.findFirst()
					.orElseThrow(() -> new LocationApiException("City and state combination doesn't match"));

			log.info("Location retrieved successfully: {}, {}, {}", city, state, country);

			return location;

		} catch (LocationApiException ex) {

			log.error("Location retrieval failed for {}, {}, {}: {}", city, state, country, ex.getMessage());

			throw ex;

		} catch (Exception ex) {

			log.error("OpenWeather location API failed for {}, {}, {}", city, state, country, ex);

			throw new LocationApiException("Unable to retrieve location from OpenWeather");
		}
	}
}