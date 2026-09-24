package com.example.demo.exception;

public class CityAlreadyExistException extends RuntimeException {
	public CityAlreadyExistException(String msg) {
		super(msg);
	}

}
