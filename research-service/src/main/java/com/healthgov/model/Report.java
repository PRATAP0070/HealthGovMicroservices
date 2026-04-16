package com.healthgov.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "reports")
public class Report {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId; // [cite: 87]

	private String scope; // [Program/Project/Grant] [cite: 87]

	@Column(columnDefinition = "TEXT")
	private String metrics; // Analytics on efficiency [cite: 87]

	private LocalDateTime generatedDate; // [cite: 87]

	// Default Constructor
	public Report() {
	}

	// Getters and Setters
	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}

	public LocalDateTime getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(LocalDateTime generatedDate) {
		this.generatedDate = generatedDate;
	}
}