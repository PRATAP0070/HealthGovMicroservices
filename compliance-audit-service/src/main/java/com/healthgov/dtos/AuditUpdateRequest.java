package com.healthgov.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.healthgov.enums.AuditStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditUpdateRequest {

	private String findings;

	@PastOrPresent(message = "date cannot be in the future.")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotNull(message = "status is required. Allowed: SCHEDULED, IN_REVIEW, COMPLETED, FOLLOW_UP_REQUIRED.")
	private AuditStatus status;
}