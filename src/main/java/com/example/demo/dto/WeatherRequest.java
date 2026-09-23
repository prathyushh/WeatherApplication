package com.example.demo.dto;

import lombok.Data;

@Data
public class WeatherRequest {
  private String city;
  private String state;
}
