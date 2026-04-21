package com.healthgov.dto;

import java.util.Map;

import com.healthgov.enums.ResourceStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhysicalResourceSummaryDto {
	private Long totalQuantity;
	private Map<ResourceStatus, Long> byStatus;
}
