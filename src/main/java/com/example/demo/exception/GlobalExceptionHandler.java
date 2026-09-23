package com.example.demo.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleCityNotFoundException(CityNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 404, ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(LocationApiException.class)
	public ResponseEntity<ErrorResponse> handleLocationApiException(LocationApiException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 502, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
	}

	@ExceptionHandler(WeatherApiException.class)
	public ResponseEntity<ErrorResponse> handleWeatherApiException(WeatherApiException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), 502, ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
	}

	@ExceptionHandler(CityAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleCityAlreadyExistsException(CityAlreadyExistsException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				"City already exists");

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

}
