package com.example.demo.exception;

public class CityAlreadyExistsException extends RuntimeException {
	public CityAlreadyExistsException(String msg) {
		super(msg);
	}

}
