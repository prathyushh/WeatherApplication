package com.example.demo.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationMessages {
	public static final String CITY_REQUIRED = "City is required";
	public static final String CITY_INVALID = "City must contain only letters, spaces, dots or hyphens";
	public static final String STATE_REQUIRED = "State is required";
	public static final String STATE_INVALID = "State must contain only letters, spaces, dots or hyphens";
	public static final String COUNTRY_CODE_REQUIRED = "Country code is required";
	public static final String COUNTRY_CODE_INVALID = "Country code must be a 2-letter country code";
	public static final String PASSWORD_INVALID = "Password must be at least 6 characters and contain only letters and numbers";
	public static final String VALIDATION_FAILED = "Validation Failed";
}