package com.healthgov.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.healthgov.dtos.ComplianceResponseDTO;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceType;
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
	public ResponseEntity<List<ComplianceResponseDTO>> listAll() {
		log.info("GET /api/v1/compliance-records");
		List<ComplianceResponseDTO> response = complianceService.getAllComplianceRecords();
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ComplianceResponseDTO> getByComplianceId(@PathVariable("id") Long complianceId) {
		log.info("GET /api/v1/compliance-records/id  Hit.....");
		
		return ResponseEntity.ok(complianceService.getComplianceById(complianceId));
	}

	@GetMapping("/{type}/{entityId}")
	public ResponseEntity<ComplianceResponseDTO> getOne(@PathVariable ComplianceType type,
			@PathVariable Long entityId) {

		log.info("GET /api/v1/compliance-records/{}/{}", type, entityId);
		ComplianceResponseDTO response = complianceService.getOneByEntityIdAndType(type, entityId);
		return ResponseEntity.ok(response);
	}

	// INSERT ONLY
	@PostMapping("/create")
	public ResponseEntity<ComplianceResponseDTO> create(@Valid @RequestBody ComplianceCreateRequest request) {
		log.info("POST /api/v1/compliance-records/create type={} entityId={}", request.getType(),
				request.getEntityId());
		ComplianceResponseDTO created = complianceService.createRecord(request);
		return ResponseEntity.ok(created);
	}

	// FULL UPDATE (by type + entityId)
	@PutMapping("/{type}/{entityId}")
	public ResponseEntity<ComplianceResponseDTO> update(@PathVariable ComplianceType type, @PathVariable Long entityId,
			@Valid @RequestBody ComplianceUpdateRequest request) {

		log.info("PUT /api/v1/compliance-records/{}/{}", type, entityId);
		return ResponseEntity.ok(complianceService.updateExisting(type, entityId, request));
	}

	// UPDATE RESULT ONLY (by type + entityId)
	@PatchMapping("/{type}/{entityId}/result")
	public ResponseEntity<ComplianceResponseDTO> patchResult(@PathVariable ComplianceType type,
			@PathVariable Long entityId, @RequestParam("result") String result) {

		log.info("PATCH /api/v1/compliance-records/{}/{}/result", type, entityId);
		return ResponseEntity.ok(complianceService.updateResultByEntityIdAndType(type, entityId, result));
	}

	// UPDATE NOTES ONLY (by type + entityId)
	@PatchMapping("/{type}/{entityId}/notes")
	public ResponseEntity<ComplianceResponseDTO> patchNotes(@PathVariable ComplianceType type,
			@PathVariable Long entityId, @RequestParam("notes") String notes) {

		log.info("PATCH /api/v1/compliance-records/{}/{}/notes", type, entityId);
		return ResponseEntity.ok(complianceService.updateNotesByEntityIdAndType(type, entityId, notes));
	}

	//Delete compliance Record
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ComplianceResponseDTO> deleteRecords(@PathVariable("id") Long complianceId) {
		log.info("DELETE /delete/id Compliance Record Delete request hit");
		return ResponseEntity.ok(complianceService.deleteById(complianceId));
	}
}