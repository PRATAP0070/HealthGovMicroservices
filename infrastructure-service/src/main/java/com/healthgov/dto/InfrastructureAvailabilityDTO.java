package com.healthgov.dto;

import com.healthgov.enums.InfrastructureType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfrastructureAvailabilityDTO {

	private InfrastructureType type;
	private Long operational;
	private Long underMaintenance;
	private Long temporarilyClosed;
	private Long decommissioned;
	private Long total;
}