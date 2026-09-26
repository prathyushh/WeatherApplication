package com.example.demo.exception;

public class WeatherApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WeatherApiException(String msg) {
		super(msg);
	}

}
