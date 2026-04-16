package com.healthgov.model;

import java.sql.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	private Date startDate;
	private Date endDate;
	private Double budget;
	private String status;

	@OneToMany(mappedBy = "program")
	private List<Enrollment> enrollments;

	@OneToMany(mappedBy = "program")
	private List<Resource> resources;

	@OneToMany(mappedBy = "program")
	private List<Infrastructure> infrastructures;
}