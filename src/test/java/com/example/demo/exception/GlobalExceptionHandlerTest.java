
package com.example.demo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

class GlobalExceptionHandlerTest {
	private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

	@Test
	void handleCityNotFoundException_shouldReturn404() {
		CityNotFoundException exception = new CityNotFoundException("City not found!");
		ResponseEntity<ErrorResponse> response = handler.handleCityNotFoundException(exception);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(404, response.getBody().getStatus());
		assertEquals("City not found!", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleLocationApiException_shouldReturn502() {
		LocationApiException exception = new LocationApiException("Unable to retrieve location from OpenWeather");
		ResponseEntity<ErrorResponse> response = handler.handleLocationApiException(exception);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(502, response.getBody().getStatus());
		assertEquals("Unable to retrieve location from OpenWeather", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleWeatherApiException_shouldReturn502() {
		WeatherApiException exception = new WeatherApiException("Unable to retrieve weather from OpenWeather");
		ResponseEntity<ErrorResponse> response = handler.handleWeatherApiException(exception);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(502, response.getBody().getStatus());
		assertEquals("Unable to retrieve weather from OpenWeather", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleCityAlreadyExistsException_shouldReturn409() {
		CityAlreadyExistsException exception = new CityAlreadyExistsException("City already exists");
		ResponseEntity<ErrorResponse> response = handler.handleCityAlreadyExistsException(exception);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(409, response.getBody().getStatus());
		assertEquals("City already exists", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleInvalidRefreshToken_shouldReturn401() {
		InvalidRefreshTokenException exception = new InvalidRefreshTokenException("Invalid refresh token");
		ResponseEntity<ErrorResponse> response = handler.handleInvalidRefreshToken(exception);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(401, response.getBody().getStatus());
		assertEquals("Invalid refresh token", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleUserAlreadyExistsException_shouldReturn409() {
		UserAlreadyExistsException exception = new UserAlreadyExistsException("User already exists");
		ResponseEntity<ErrorResponse> response = handler.handleUserAlreadyExistsException(exception);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(409, response.getBody().getStatus());
		assertEquals("User already exists", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleDataIntegrityViolation_shouldReturn409() {
		DataIntegrityViolationException exception = new DataIntegrityViolationException(
				"Database constraint violation");
		ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolation(exception);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(409, response.getBody().getStatus());
		assertEquals("Database constraint violation", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleDatabaseFailure_shouldReturn503() {
		DataAccessException exception = new DataAccessException("Database unavailable") {
			private static final long serialVersionUID = 1L;
		};
		ResponseEntity<ErrorResponse> response = handler.handleDatabaseFailure(exception);
		assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(503, response.getBody().getStatus());
		assertEquals("Database service is temporarily unavailable", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}

	@Test
	void handleBadCredentials_shouldReturn401() {
		BadCredentialsException exception = new BadCredentialsException("Invalid username or password");
		ResponseEntity<ErrorResponse> response = handler.handleBadCredentials(exception);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(401, response.getBody().getStatus());
		assertEquals("Invalid username or password", response.getBody().getMessage());
		assertNotNull(response.getBody().getTimestamp());
	}
}
