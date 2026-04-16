package com.healthgov.model;

import java.time.LocalDateTime;

import com.healthgov.enums.DocumentType;
import com.healthgov.enums.VerificationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citizen_document")
@Data
@NoArgsConstructor
public class CitizenDocument {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long documentId;

	private String documentName;

	@Enumerated(EnumType.STRING)
	private DocumentType docType;

	private String fileURI;

	private LocalDateTime uploadedDate;

	@Enumerated(EnumType.STRING)
	private VerificationStatus verificationStatus;

	@ManyToOne
	@JoinColumn(name = "citizen_id")
	private Citizen citizen;
}