package com.healthgov.dto;

import lombok.Data;

@Data
public class ProgramReportMetrics {

	private long totalPrograms;
	private long activePrograms;
	private long completedPrograms;
	private long upcomingPrograms;
}
