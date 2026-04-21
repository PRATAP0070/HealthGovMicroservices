package com.healthgov.dto;

import java.util.Map;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceOverviewDto {
	private Long totalResources;
	private Map<ResourceType, Long> byType;
	private Map<ResourceStatus, Long> byStatus;
}