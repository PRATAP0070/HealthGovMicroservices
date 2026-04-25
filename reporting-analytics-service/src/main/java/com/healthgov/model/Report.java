package com.healthgov.model;

import java.time.LocalDateTime;

import com.healthgov.enums.ReportScope;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;

	@Enumerated(EnumType.STRING)
	private ReportScope scope;

	@Lob
	private String metrics; // JSON stored as String

	private LocalDateTime generatedDate;
}