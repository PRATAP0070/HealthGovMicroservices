package com.healthgov.dto;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceUpdateRequest {

	@NotNull
	private ResourceType type;

	@PositiveOrZero
	private Integer quantity;

	@NotNull
	private ResourceStatus status;
}
