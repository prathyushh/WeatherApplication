package com.example.demo.service;

import com.example.demo.dto.GeocodingApiResponse;

public interface LocationProvider {
	GeocodingApiResponse findLocation(String city,String state,String country);

}
