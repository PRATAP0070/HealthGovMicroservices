package com.healthgov.dtos;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ResearchProjectResponse implements ComplianceEntityDTO {

	private Long projectId;
	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
	private String reason;

	private Long researcherId;
	private String researcherName;

	@Override
	@JsonIgnore
	public Long getOwnerId() {
		return researcherId;
	}
}
