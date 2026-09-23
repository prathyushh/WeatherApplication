package com.example.demo.exception;

public class CityNotFoundException extends RuntimeException{
	public CityNotFoundException(String msg) {
		super(msg);
	}

}
