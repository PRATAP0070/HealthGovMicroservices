package com.healthgov.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class ComplianceEntityFallbackDTO implements ComplianceEntityDTO {

    private Long id;
    private String title;
    private String status;
	@Override
	@JsonIgnore
	public Long getOwnerId() {
		// TODO Auto-generated method stub
		return id;
	}
	@Override
	@JsonIgnore
	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}
	public void setId(Long entityId) {
		// TODO Auto-generated method stub
		
	}

}