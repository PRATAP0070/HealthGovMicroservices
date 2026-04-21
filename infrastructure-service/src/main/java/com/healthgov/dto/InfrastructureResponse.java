package com.healthgov.dto;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InfrastructureResponse {

	private Long infraId;
	private Long programId;
	private InfrastructureType type;
	private String location;
	private int capacity;
	private InfrastructureStatus status;

}
