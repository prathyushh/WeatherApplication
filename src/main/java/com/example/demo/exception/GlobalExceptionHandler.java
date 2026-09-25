package com.example.demo.exception;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

	@ExceptionHandler(CityAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleCityAlreadyExistsException(CityAlreadyExistException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(), ex.getMessage());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(UserAlreadyExistException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistException ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).findFirst()
				.orElse("Validation failed");

		return ResponseEntity.badRequest().body(message);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
				"City already exists");

		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), 401, "Invalid username or password");

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
				ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

}
