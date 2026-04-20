package com.healthgov.dtos;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class HealthProgramResponseDTO {

	private Long programId;
	private String title;
	private String description;
	private Date startDate;
	private Date endDate;
	private Double budget;
	private String status;

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
		private String type;
		private Integer quantity;
		private String status;
	}

	@Data
	public static class InfrastructureDTO {
		private Long infraId;
		private String type;
		private String location;
		private Integer capacity;
		private String status;
	}
}