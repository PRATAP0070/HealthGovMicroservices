package com.healthgov.model;

import java.time.LocalDate;

import com.healthgov.enums.GrantStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GrantApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long applicationId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "researcher_id", nullable = false)
	private Users researcher;

	@ManyToOne(optional = false)
	@JoinColumn(name = "project_id", nullable = false)
	private ResearchProject project;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GrantStatus status;

	@Column(name = "submitted_date", nullable = false)
	private LocalDate submittedDate;
}