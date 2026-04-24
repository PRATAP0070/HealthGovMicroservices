package com.healthgov.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfrastructureReportResponseDTO {

	private InfrastructureSummaryDTO summary;
	private List<InfrastructureAvailabilityDTO> infrastructureAvailability;

}