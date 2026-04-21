package com.healthgov.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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