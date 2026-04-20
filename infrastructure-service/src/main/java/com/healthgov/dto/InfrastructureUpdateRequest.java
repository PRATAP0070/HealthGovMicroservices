package com.healthgov.dto;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InfrastructureUpdateRequest {

	@NotNull
	private InfrastructureType type;

	@NotBlank
	private String location;

	@PositiveOrZero
	private int capacity;

	@NotNull
	private InfrastructureStatus status;

}
