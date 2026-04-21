package com.healthgov.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.healthgov.enums.AuditStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditCreateRequest {

	@NotNull(message = "officerId is required.")
	private Long officerId;

	@NotBlank(message = "Audit scope is required. Use PROGRAM:<id>, PROJECT:<id>, or GRANT:<id>.")
	private String scope;

	private String findings;

	@PastOrPresent(message = "date cannot be in the future.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotNull(message = "Audit Status is required. Allowed: SCHEDULED, IN_REVIEW, COMPLETED, FOLLOW_UP_REQUIRED.")
	private AuditStatus status;
}