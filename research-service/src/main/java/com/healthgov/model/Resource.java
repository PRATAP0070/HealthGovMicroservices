package com.healthgov.model;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "resources")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Resource {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generate, auto-increment ID
	private Long resourceId;

	@ManyToOne
	@JoinColumn(name = "programId")
	private HealthProgram program;

	@NotNull
	@Enumerated(EnumType.STRING) // store enum as text
	private ResourceType type; // FUNDS/LAB/EQUIPMENT

	@NotNull
	@PositiveOrZero
	private Integer quantity;

	@NotNull
	@Enumerated(EnumType.STRING) // store enum as text
	private ResourceStatus status; // ACTIVE,INACTIVE,DECOMMISSIONED,PENDING
}
