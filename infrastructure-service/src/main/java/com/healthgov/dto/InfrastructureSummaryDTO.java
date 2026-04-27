package com.healthgov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InfrastructureSummaryDTO {

	private Long totalInfrastructure;
	private Long hospital;
	private Long lab;
	private Long center;
}