package com.healthgov.dtos;

import java.time.LocalDate;

import com.healthgov.enums.ComplianceType;

import lombok.Data;

@Data
public class ComplianceResponseDTO {

	private ComplianceType type;
	private Long entityId;
	private String result;
	private LocalDate date;
	private String notes;
	private Object entity;

}
