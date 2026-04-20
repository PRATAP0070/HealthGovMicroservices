package com.healthgov.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearchProjectCreateRequest {

	@NotBlank(message = "title is required")
	private String title;

	@NotBlank(message = "description is required")
	private String description;

	@NotNull(message = "researcherId is required")
	private Long researcherId;

	@NotNull(message = "startDate is required")
	private LocalDate startDate;

	@NotNull(message = "endDate is required")
	private LocalDate endDate;
}