package com.healthgov.dto;

import lombok.Data;

@Data
public class ProgramDTO {

	private Long programId;
	private String status; // ACTIVE, COMPLETED, UPCOMING
}
