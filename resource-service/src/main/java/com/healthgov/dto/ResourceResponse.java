package com.healthgov.dto;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceResponse {
	private Long resourceId;
	private Long programId;
	private ResourceType type;
	private Integer quantity;
	private ResourceStatus status;
}
