package com.healthgov.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.model.Grants;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.service.GrantService;
import com.healthgov.service.ResearchProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/grant")
@RequiredArgsConstructor
public class GrantController {

	private final GrantService grantService;
	private final ResearchProjectService projectService;

	// Get grant by ID
	@GetMapping("/{grantId}")
	public ResponseEntity<Grants> getGrantById(@PathVariable Long grantId) {

		return ResponseEntity.ok(grantService.getGrantById(grantId));
	}

	// ✅ Check if grant exists by grant ID
	@GetMapping("/{grantId}/exists")
	public ResponseEntity<Boolean> grantExists(@PathVariable Long grantId) {

		return ResponseEntity.ok(grantService.grantExists(grantId));
	}

	// Check if PROJECT exists by projectId
	@GetMapping("/project/{projectId}/exists")
	public ResponseEntity<Boolean> projectExists(@PathVariable Long projectId) {

		return ResponseEntity.ok(projectService.projectExists(projectId));
	}

	// Get PROJECT by projectId
	@GetMapping("/project/{projectId}")
	public ResponseEntity<ResearchProjectResponse> getProjectById(@PathVariable Long projectId) {

		return ResponseEntity.ok(projectService.get(projectId));
	}
}