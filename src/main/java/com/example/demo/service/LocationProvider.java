package com.example.demo.service;

import com.example.demo.dto.GeocodingLocation;

public interface LocationProvider {
	GeocodingLocation getLocation(String city,String state,String country);

}
