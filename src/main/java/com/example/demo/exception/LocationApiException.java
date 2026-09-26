package com.example.demo.exception;

public class LocationApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public LocationApiException(String msg) {
		super(msg);
	}

}
