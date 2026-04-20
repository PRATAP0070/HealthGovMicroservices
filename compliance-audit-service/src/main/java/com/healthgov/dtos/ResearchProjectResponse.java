package com.healthgov.dtos;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ResearchProjectResponse {

	private Long projectId;
	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
	private String reason;

	private Long researcherId;
	private String researcherName;
}
