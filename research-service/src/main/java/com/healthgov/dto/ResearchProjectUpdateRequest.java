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
public class ResearchProjectUpdateRequest {

	@NotNull(message = "projectId is required")
	private Long projectId;

	@NotBlank(message = "title is required")
	private String title;

	@NotBlank(message = "description is required")
	private String description;

	@NotNull(message = "startDate is required")
	private LocalDate startDate;

	@NotNull(message = "endDate is required")
	private LocalDate endDate;

	private String status;

	// Required only when status = REJECTED (PM flow)
	private String reason;
}