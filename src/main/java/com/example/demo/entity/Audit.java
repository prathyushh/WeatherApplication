package com.example.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audits")
public class Audit {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
	 @Column(nullable = false)
	 private Long userId;
	 @Column(nullable = false)
	 private String action;
	 private LocalDateTime timestamp;
	 private String details;
}
