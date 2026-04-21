package com.healthgov.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

@Data
public class ComplianceUpdateRequest {

	@NotBlank(message = "Compliance result is required and must be one of COMPLIANT, PARTIAL, NON_COMPLIANT")
	private String result;

	@PastOrPresent(message = "date cannot be in the future")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotBlank(message = "Compliance notes is required")
	private String notes;
}