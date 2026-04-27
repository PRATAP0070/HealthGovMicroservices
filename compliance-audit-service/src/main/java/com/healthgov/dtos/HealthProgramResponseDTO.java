package com.healthgov.dtos;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.enums.ProgramStatus;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

import lombok.Data;

@Data
public class HealthProgramResponseDTO implements ComplianceEntityDTO{
	private Long managerId;
	private Long programId;
	private String title;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;
	private Double budget;
	private ProgramStatus status;

	private List<EnrollmentDTO> enrollments;
	private List<ResourceDTO> resources;
	private List<InfrastructureDTO> infrastructures;
	
	

	 @Override
	 @JsonIgnore 
	 public Long getOwnerId() {
	        return managerId;
	 }


	@Data
	public static class EnrollmentDTO {
		private Long enrollmentId;
		private Long citizenId;
		LocalDate enrolledDate; // maps from Enrollment.date
		private String status;
	}

	@Data
	public static class ResourceDTO {
		private Long resourceId;
		private Long programId;
		private ResourceType type;
		private Integer quantity;
		private ResourceStatus status;
	}

	@Data
	public static class InfrastructureDTO {
		private Long infraId;
		private Long programId;
		private InfrastructureType type;
		private String location;
		private int capacity;
		private InfrastructureStatus status;
	}
}