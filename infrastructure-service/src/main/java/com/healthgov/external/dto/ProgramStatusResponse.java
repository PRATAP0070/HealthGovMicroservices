package com.healthgov.external.dto;

import com.healthgov.enums.ProgramStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgramStatusResponse {
	long programId;
	double budget;
	ProgramStatus status;
}
