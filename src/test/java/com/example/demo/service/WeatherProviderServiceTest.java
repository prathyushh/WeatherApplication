
package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.example.demo.dto.Main;
import com.example.demo.dto.Weather;
import com.example.demo.dto.WeatherApiResponse;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.dto.Wind;
import com.example.demo.entity.City;
import com.example.demo.exception.WeatherApiException;

@ExtendWith(MockitoExtension.class)
class WeatherProviderServiceTest {
	@Mock
	private RestClient restClient;
	@Mock
	private RestClient.RequestHeadersUriSpec request;
	@Mock
	private RestClient.RequestHeadersSpec requestHeaders;
	@Mock
	private RestClient.ResponseSpec responseSpec;
	@InjectMocks
	private WeatherProviderService weatherProviderService;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(weatherProviderService, "apiKey", "test-api-key");
	}

	@Test
	void getWeather_shouldReturnWeather() {
		City city = City.builder().city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();
		Main main = new Main();
		main.setTemp(30.5);
		main.setFeels_like(31.2);
		main.setHumidity(70);
		Weather weather = new Weather();
		weather.setMain("Clouds");
		weather.setDescription("broken clouds");
		Wind wind = new Wind();
		wind.setSpeed(4.5);
		WeatherApiResponse apiResponse = new WeatherApiResponse();
		apiResponse.setMain(main);
		apiResponse.setWeather(List.of(weather));
		apiResponse.setWind(wind);
		when(restClient.get()).thenReturn(request);
		when(request.uri(any(java.util.function.Function.class))).thenReturn(requestHeaders);
		when(requestHeaders.retrieve()).thenReturn(responseSpec);
		when(responseSpec.body(WeatherApiResponse.class)).thenReturn(apiResponse);
		WeatherResponse result = weatherProviderService.getWeather(city);
		assertNotNull(result);
		assertEquals("Kota", result.getCity());
		assertEquals("Rajasthan", result.getState());
		assertEquals("IN", result.getCountryCode());
		assertEquals(30.5, result.getTemperature());
		assertEquals(31.2, result.getFeelsLike());
		assertEquals(70, result.getHumidity());
		assertEquals("Clouds", result.getMain());
		assertEquals("broken clouds", result.getDescription());
		assertEquals(4.5, result.getWindSpeed());
		verify(restClient).get();
	}

	@Test
	void getWeather_shouldThrowException_whenApiFails() {
		City city = City.builder().city("Kota").state("Rajasthan").countryCode("IN").latitude(25.2138)
				.longitude(75.8648).build();
		when(restClient.get()).thenThrow(new RuntimeException("API failed"));
		WeatherApiException exception = assertThrows(WeatherApiException.class,
				() -> weatherProviderService.getWeather(city));

		assertEquals("Unable to retrieve weather from OpenWeather", exception.getMessage());
	}
}
