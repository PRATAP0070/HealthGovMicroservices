package com.healthgov.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramManagerDecisionRequest {

	@NotBlank(message = "decision is required")
	private String decision;

	private String reason;

	private Double amount;
}