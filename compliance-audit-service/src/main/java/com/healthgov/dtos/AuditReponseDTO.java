package com.healthgov.dtos;

import java.time.LocalDate;

import com.healthgov.enums.AuditStatus;

import lombok.Data;

@Data
public class AuditReponseDTO {
	private Long auditId;

	private UserResponseDto officer;

	private String scope;

	private String findings;

	private LocalDate date;

	private AuditStatus status;
}