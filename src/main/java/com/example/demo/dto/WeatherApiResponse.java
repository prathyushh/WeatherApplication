package com.example.demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class WeatherApiResponse {
	private Main main;
	private List<Weather> weather;
	private Wind wind;
}
