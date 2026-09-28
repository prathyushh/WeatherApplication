
package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.GeocodingApiResponse;
import com.example.demo.exception.LocationApiException;

@ExtendWith(MockitoExtension.class)
class LocationProviderServiceTest {
	@Mock
	private RestClient restClient;
	@Mock
	private RestClient.RequestHeadersUriSpec request;
	@Mock
	private RestClient.RequestHeadersSpec requestHeaders;
	@Mock
	private RestClient.ResponseSpec responseSpec;
	@InjectMocks
	private LocationProviderService locationProviderService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(locationProviderService, "apiKey", "test-api-key");
	}

	@Test
	void findLocation_shouldReturnLocation() {
		GeocodingApiResponse location = new GeocodingApiResponse();
		location.setCity("Kota");
		location.setState("Rajasthan");
		location.setCountry("IN");
		location.setLat(25.2138);
		location.setLon(75.8648);
		GeocodingApiResponse[] locations = { location };
		when(restClient.get()).thenReturn(request);
		when(request.uri(any(java.util.function.Function.class))).thenReturn(requestHeaders);
		when(requestHeaders.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingApiResponse[].class)).thenReturn(locations);
		GeocodingApiResponse result = locationProviderService.findLocation("Kota", "Rajasthan", "IN");
		assertNotNull(result);
		assertEquals("Kota", result.getCity());
		assertEquals("Rajasthan", result.getState());
		assertEquals("IN", result.getCountry());
		assertEquals(25.2138, result.getLat());
		assertEquals(75.8648, result.getLon());
		verify(restClient).get();
	}

	@Test
	void findLocation_shouldThrowException_whenLocationNotFound() {
		GeocodingApiResponse location = new GeocodingApiResponse();
		location.setCity("Kota");
		location.setState("Karnataka");
		location.setCountry("IN");
		when(restClient.get()).thenReturn(request);
		when(request.uri(any(java.util.function.Function.class))).thenReturn(requestHeaders);
		when(requestHeaders.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(GeocodingApiResponse[].class)).thenReturn(new GeocodingApiResponse[] { location });
		LocationApiException exception = assertThrows(LocationApiException.class,
				() -> locationProviderService.findLocation("Kota", "Rajasthan", "IN"));
		assertEquals("City and state combination doesn't match", exception.getMessage());
	}

	@Test
	void findLocation_shouldThrowException_whenApiFails() {
		when(restClient.get()).thenThrow(new RuntimeException("API failed"));
		LocationApiException exception = assertThrows(LocationApiException.class,
				() -> locationProviderService.findLocation("Kota", "Rajasthan", "IN"));
		assertEquals("Unable to retrieve location from OpenWeather", exception.getMessage());
	}
}
