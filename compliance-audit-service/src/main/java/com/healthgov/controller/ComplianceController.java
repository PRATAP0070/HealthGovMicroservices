package com.healthgov.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceType;
import com.healthgov.models.ComplianceRecord;
import com.healthgov.services.ComplianceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/compliance-records")
@Validated
public class ComplianceController {

	private final ComplianceService complianceService;
	private static final Logger log = LoggerFactory.getLogger(ComplianceController.class);

	public ComplianceController(ComplianceService complianceService) {
		this.complianceService = complianceService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<ComplianceRecord>> listAll() {
		log.info("GET /api/v1/compliance-records");
		return ResponseEntity.ok(complianceService.getAllComplianceRecords());
	}

	@GetMapping("/{type}/{entityId}")
	public ResponseEntity<ComplianceRecord> getOne(@PathVariable ComplianceType type, @PathVariable Long entityId) {

		log.info("GET /api/v1/compliance-records/{}/{}", type, entityId);
		return ResponseEntity.ok(complianceService.getOneByEntityIdAndType(type, entityId));
	}

	// INSERT ONLY
	@PostMapping("/create")
	public ResponseEntity<ComplianceRecord> create(@Valid @RequestBody ComplianceCreateRequest request) {
		log.info("POST /api/v1/compliance-records/create type={} entityId={}", request.getType(),
				request.getEntityId());
		ComplianceRecord created = complianceService.createRecord(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// FULL UPDATE (by type + entityId)
	@PutMapping("/{type}/{entityId}")
	public ResponseEntity<ComplianceRecord> update(@PathVariable ComplianceType type, @PathVariable Long entityId,
			@Valid @RequestBody ComplianceUpdateRequest request) {

		log.info("PUT /api/v1/compliance-records/{}/{}", type, entityId);
		return ResponseEntity.ok(complianceService.updateExisting(type, entityId, request));
	}

	// UPDATE RESULT ONLY (by type + entityId)
	@PatchMapping("/{type}/{entityId}/result")
	public ResponseEntity<ComplianceRecord> patchResult(@PathVariable ComplianceType type, @PathVariable Long entityId,
			@RequestParam("result") String result) {

		log.info("PATCH /api/v1/compliance-records/{}/{}/result", type, entityId);
		return ResponseEntity.ok(complianceService.updateResultByEntityIdAndType(type, entityId, result));
	}

	// UPDATE NOTES ONLY (by type + entityId)
	@PatchMapping("/{type}/{entityId}/notes")
	public ResponseEntity<ComplianceRecord> patchNotes(@PathVariable ComplianceType type, @PathVariable Long entityId,
			@RequestParam("notes") String notes) {

		log.info("PATCH /api/v1/compliance-records/{}/{}/notes", type, entityId);
		return ResponseEntity.ok(complianceService.updateNotesByEntityIdAndType(type, entityId, notes));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ComplianceRecord> deleteRecords(@PathVariable("id") Long complianceId) {
		return ResponseEntity.ok(complianceService.deleteById(complianceId));
	}
}