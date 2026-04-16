package com.healthgov.model;

import java.time.LocalDate;
import java.util.List;

import com.healthgov.enums.ProjectStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchProject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long projectId;

	private String title;
	private String description;

	@ManyToOne
	@JoinColumn(name = "researcherId", nullable = false)
	private Users researcher;

	private LocalDate startDate;
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	private ProjectStatus status;

	private String reason;

	@OneToMany(mappedBy = "project")
	private List<GrantApplication> grantApplications;

	@OneToOne(mappedBy = "project", cascade = CascadeType.ALL)
	private Grants grant;
}
