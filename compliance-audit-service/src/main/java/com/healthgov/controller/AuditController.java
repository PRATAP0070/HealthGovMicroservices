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
import com.healthgov.dtos.AuditUpdateRequest;
import com.healthgov.models.Audit;
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
	public ResponseEntity<Audit> createAudit(@Valid @RequestBody AuditCreateRequest request) {
		log.info("POST /api/v1/audits officerId={} scope={}", request.getOfficerId(), request.getScope());
		Audit created = auditService.createAudit(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// UPDATE ENTIRE AUDIT (findings/date/status)
	@PutMapping("/update/{auditId}")
	public ResponseEntity<Audit> updateAudit(@PathVariable Long auditId,
			@Valid @RequestBody AuditUpdateRequest request) {

		log.info("PUT /api/v1/audits/{}", auditId);
		return ResponseEntity.ok(auditService.updateAudit(auditId, request));
	}

	// PATCH ONLY STATUS
	@PatchMapping("/update/{auditId}/status")
	public ResponseEntity<Audit> patchStatus(@PathVariable Long auditId, @RequestParam("status") String status) {

		log.info("PATCH /api/v1/audits/{}/status status={}", auditId, status);
		return ResponseEntity.ok(auditService.updateStatus(auditId, status));
	}

	// PATCH ONLY FINDINGS
	@PatchMapping("/update/{auditId}/findings")
	public ResponseEntity<Audit> patchFindings(@PathVariable Long auditId, @RequestParam("findings") String findings) {

		log.info("PATCH /api/v1/audits/{}/findings", auditId);
		return ResponseEntity.ok(auditService.updateFindings(auditId, findings));
	}

	// GET ONE AUDIT
	@GetMapping("/{auditId}")
	public ResponseEntity<Audit> getAudit(@PathVariable Long auditId) {
		log.info("GET /api/v1/audits/{}", auditId);
		return ResponseEntity.ok(auditService.getAudit(auditId));
	}

	@GetMapping("/all")
	public ResponseEntity<List<Audit>> getAllAudits() {
		return ResponseEntity.ok(auditService.getAllAudits());
	}

	@GetMapping("/byOfficer/{id}")
	public ResponseEntity<List<Audit>> getAllAuditsByOfficer(@PathVariable("id") Long officerId) {
		return ResponseEntity.ok(auditService.getAllAuditsByOfficer(officerId));
	}

}