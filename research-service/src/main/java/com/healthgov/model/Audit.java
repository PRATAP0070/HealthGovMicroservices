package com.healthgov.model;

import java.time.LocalDate;

import com.healthgov.enums.AuditStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit", uniqueConstraints = {
		@UniqueConstraint(name = "uk_audit_officer_scope", columnNames = { "officer_id", "scope" }) }, indexes = {
				@Index(name = "ix_audit_officer_id", columnList = "officer_id"),
				@Index(name = "ix_audit_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long auditId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "officer_id", nullable = false)
	private Users officer;

	@NotBlank(message = "Audit scope is required.")
	@Column(nullable = false)
	private String scope;

	@NotBlank(message = "Audit findings are required.")
	@Column(nullable = false)
	private String findings;

	@NotNull(message = "Audit date is required.")
	@PastOrPresent(message = "date cannot be in the future.")
	@Column(nullable = false)
	private LocalDate date;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AuditStatus status;
}
