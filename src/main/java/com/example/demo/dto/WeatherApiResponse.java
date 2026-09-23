package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class WeatherApiResponse {
	private Main main;
	private List<Weather> weather;
	private Wind wind;

	@Data
	public static class Main {
		private Double temp;
		private Double feels_like;
		private Integer humidity;
	}

	@Data
	public static class Weather {
		private String main;
		private String description;
	}

	@Data
	public static class Wind {
		private Double speed;
	}

}
