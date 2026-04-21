package com.healthgov.dto;

import java.util.Map;

import com.healthgov.enums.ResourceType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceSummaryResponse {
	private Long programId;
	private ResourceOverviewDto overview;
	private FinancialResourceSummaryDto financialResources;
	private Map<ResourceType, PhysicalResourceSummaryDto> physicalResources;
}