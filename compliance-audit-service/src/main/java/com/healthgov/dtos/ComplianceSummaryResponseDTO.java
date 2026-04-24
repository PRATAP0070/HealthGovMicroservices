package com.healthgov.dtos;

import java.util.Map;

import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComplianceSummaryResponseDTO {

	private long totalRecords;
	private Map<ComplianceResult, Long> byResult;
	private Map<ComplianceType, Long> byType;
}
