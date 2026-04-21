package com.healthgov.dto;

import java.util.Map;

import com.healthgov.enums.ResourceStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialResourceSummaryDto {
	private String currency;
	private Long totalAmount;
	private Map<ResourceStatus, Long> breakdown;
}