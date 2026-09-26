package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

	Optional<City> findByCityIgnoreCaseAndStateIgnoreCase(String cityname, String state);

	Page<City> findAll(Pageable pageable);

}
