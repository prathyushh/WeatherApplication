package com.example.demo.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionMessages {
	public static final String USER_NOT_FOUND = "User not found";
	public static final String USER_ALREADY_EXISTS = "User already exists";
	public static final String CITY_NOT_FOUND = "City not found!";
	public static final String CITY_ALREADY_EXISTS = "City already exists!";
	public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";
	public static final String API_LOCATION_ERROR = "Unable to retrieve location from OpenWeather";
	public static final String API_CITYSTATECOMBINATION_ERROR = "City and state combination doesn't match";
	public static final String API_WEATHER_ERROR = "Unable to retrieve weather from OpenWeather";
	public static final String FORBIDDEN = "Forbidden";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static final String JWT_TOKEN_EXPIRED = "JWT token has expired";
	public static final String INVALID_JWT_TOKEN = "Invalid JWT token";
	public static final String USERNAMEPASSWORD_INVALID = "Invalid username or password";
	public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";
	public static final String DATABASE_VIOLATION = "Database constraint violation";
	public static final String DATABASE_UNAVAILABLE = "Database unavailable";
}