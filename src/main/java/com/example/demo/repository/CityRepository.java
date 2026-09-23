package com.example.demo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.City;
@Repository
public interface CityRepository extends JpaRepository<City, Long> {

	City findByCityAndState(String cityname, String state);
	Page<City> findAll(Pageable pageable);

}
