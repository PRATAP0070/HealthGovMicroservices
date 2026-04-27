package com.healthgov.dtos;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class GrantResponseDto implements ComplianceEntityDTO {

	private Long grantId;
	private Double amount;
	private LocalDateTime grantedAt;
	private Long projectId;
	private Long researcherId;
	private String status;

	@Override
	@JsonIgnore
	public Long getOwnerId() {
		return researcherId;
	}

	@Override
	public String getTitle() {

		return grantId + ":" + status;
	}

}
