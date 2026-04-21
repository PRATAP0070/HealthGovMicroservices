package com.healthgov.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrantResponseDto {

	private Long grantId;
	private Double amount;
	private LocalDateTime grantedAt;
	private Long projectId;
	private Long researcherId;
	private String status;
}
