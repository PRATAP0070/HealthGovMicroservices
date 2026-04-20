package com.healthgov.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.AuditReponseDTO;
import com.healthgov.dtos.AuditUpdateRequest;
import com.healthgov.services.AuditService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/audits")
@Validated
public class AuditController {

	private final AuditService auditService;
	private static final Logger log = LoggerFactory.getLogger(AuditController.class);

	public AuditController(AuditService auditService) {
		this.auditService = auditService;
	}

	// CREATE AUDIT
	@PostMapping("/create")
	public ResponseEntity<AuditReponseDTO> createAudit(@Valid @RequestBody AuditCreateRequest request) {
		log.info("POST /api/v1/audits officerId={} scope={}", request.getOfficerId(), request.getScope());
		AuditReponseDTO created = auditService.createAudit(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// UPDATE ENTIRE AUDIT (findings/date/status)
	@PutMapping("/update/{auditId}")
	public ResponseEntity<AuditReponseDTO> updateAudit(@PathVariable Long auditId,
			@Valid @RequestBody AuditUpdateRequest request) {

		log.info("PUT /api/v1/audits/{}", auditId);
		AuditReponseDTO response = auditService.updateAudit(auditId, request);
		return ResponseEntity.ok(response);
	}

	// PATCH ONLY STATUS
	@PatchMapping("/update/{auditId}/status")
	public ResponseEntity<AuditReponseDTO> patchStatus(@PathVariable Long auditId,
			@RequestParam("status") String status) {

		log.info("PATCH /api/v1/audits/{}/status status={}", auditId, status);
		AuditReponseDTO response = auditService.updateStatus(auditId, status);
		return ResponseEntity.ok(response);
	}

	// PATCH ONLY FINDINGS
	@PatchMapping("/update/{auditId}/findings")
	public ResponseEntity<AuditReponseDTO> patchFindings(@PathVariable Long auditId,
			@RequestParam("findings") String findings) {

		log.info("PATCH /api/v1/audits/{}/findings", auditId);
		AuditReponseDTO response = auditService.updateFindings(auditId, findings);
		return ResponseEntity.ok(response);
	}

	// GET ONE AUDIT
	@GetMapping("/{auditId}")
	public ResponseEntity<AuditReponseDTO> getAudit(@PathVariable Long auditId) {
		log.info("GET /api/v1/audits/{}", auditId);
		AuditReponseDTO response = auditService.getAudit(auditId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/all")
	public ResponseEntity<List<AuditReponseDTO>> getAllAudits() {
		log.info("GET /api/v1/all  Hit .");
		List<AuditReponseDTO> response = auditService.getAllAudits();
		return ResponseEntity.ok(response);
	}

	@GetMapping("/byOfficer/{id}")
	public ResponseEntity<List<AuditReponseDTO>> getAllAuditsByOfficer(@PathVariable("id") Long officerId) {
		log.info("GET /api/v1/byOfficer/id request Hit for Id : {}", officerId);
		List<AuditReponseDTO> response = auditService.getAllAuditsByOfficer(officerId);
		return ResponseEntity.ok(response);
	}

}