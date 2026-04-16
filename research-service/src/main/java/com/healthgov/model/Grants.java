package com.healthgov.model;

import java.time.LocalDateTime;

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
public class Grants {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long grantId;

	@OneToOne(optional = false)
	@JoinColumn(name = "project_id", nullable = false, unique = true)
	private ResearchProject project;

	@ManyToOne(optional = false)
	@JoinColumn(name = "researcher_id", nullable = false)
	private Users researcher;

	private Double amount;

	@Column(name = "granted_at", nullable = false)
	private LocalDateTime date;

	@Enumerated(EnumType.STRING)
	private GrantStatus status;
}
