package com.healthgov.model;

import java.time.LocalDate;

import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "compliance_record", uniqueConstraints = {
		@UniqueConstraint(name = "uq_compliance_type_entity", columnNames = { "type", "entity_id" }) }, indexes = {
				@Index(name = "ix_compliance_record_entity", columnList = "entity_id"),
				@Index(name = "ix_compliance_record_type", columnList = "type"),
				@Index(name = "ix_compliance_record_date", columnList = "date") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long complianceId;

	@NotNull
	@Column(name = "entity_id", nullable = false)
	private Long entityId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ComplianceType type;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private ComplianceResult result;

	@NotNull
	@Column(nullable = false)
	private LocalDate date;

	@NotBlank
	@Column(nullable = false, length = 2000)
	private String notes;
}