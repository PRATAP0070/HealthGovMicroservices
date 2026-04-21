package com.healthgov.model;

import java.time.LocalDate;

import com.healthgov.enums.ProgramStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HealthProgram {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long programId;

	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private Double budget;
	
	@Enumerated(EnumType.STRING)
	private ProgramStatus status;

}