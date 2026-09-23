package com.example.demo.exception;

public class WeatherApiException extends RuntimeException{
	public WeatherApiException(String msg) {
		super(msg);
	}

}
