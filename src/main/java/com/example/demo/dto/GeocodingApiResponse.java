package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GeocodingApiResponse {
	@JsonProperty("name")
	private String city;
	private Double lat;
	private Double lon;
	private String state;
	private String country;

}
