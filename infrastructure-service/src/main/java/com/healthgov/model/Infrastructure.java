package com.healthgov.model;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Infrastructure {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate, auto-increment ID
	private Long infraId;

	@NotNull
	private Long programId;

	@NotNull
	@Enumerated(EnumType.STRING) // store enum as text, not number
	private InfrastructureType type; // HOSPITAL, LAB, CENTER

	@NotBlank
	private String location;

	@NotNull
	@PositiveOrZero
	private int capacity;

	@NotNull
	@Enumerated(EnumType.STRING)
	private InfrastructureStatus status; // OPERATIONAL, UNDER_MAINTENANCE, TEMPORARILY_CLOSED, DECOMMISSIONED

}
