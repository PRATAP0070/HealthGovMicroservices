package com.healthgov.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.healthgov.enums.InfrastructureType;
import com.healthgov.enums.ProgramStatus;
import com.healthgov.enums.ResourceType;

import lombok.Data;

@Data
public class HealthProgramResponseDTO {
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

	@Data
	public static class EnrollmentDTO {
		private Long enrollmentId;
		private Long citizenId;
		private String citizenName;
		private Date enrolledDate; // maps from Enrollment.date
		private String status;
	}

	@Data
	public static class ResourceDTO {
		private Long resourceId;
		private ResourceType type;
		private Integer quantity;
		private String status;
	}

	@Data
	public static class InfrastructureDTO {
		private Long infraId;
		private InfrastructureType type;
		private String location;
		private Integer capacity;
		private String status;
	}
}