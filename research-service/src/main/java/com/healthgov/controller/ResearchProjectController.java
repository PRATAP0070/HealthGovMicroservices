package com.healthgov.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthgov.dto.ResearchProjectCreateRequest;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.dto.ResearchProjectUpdateRequest;
import com.healthgov.service.ResearchProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/research")
@RequiredArgsConstructor
public class ResearchProjectController {

	private final ResearchProjectService service;

	// Create project
	@PostMapping("/createProject")
	public ResponseEntity<List<String>> create(@Valid @RequestBody ResearchProjectCreateRequest req) {

		log.info("CREATE project request for researcherId={}", req.getResearcherId());

		ResearchProjectResponse created = service.create(req);

		return ResponseEntity.ok(List.of("Project created successfully", "Project ID: " + created.getProjectId()));
	}

	// Update project
	@PutMapping("/updateProject")
	public ResponseEntity<List<String>> update(@Valid @RequestBody ResearchProjectUpdateRequest req) {

		log.info("UPDATE project request for projectId={}", req.getProjectId());

		service.update(req);

		return ResponseEntity.ok(List.of("Project updated successfully"));
	}

	// List projects
	@GetMapping("/projects")
	public List<ResearchProjectResponse> list(@RequestParam(required = false) String status) {

		log.info("Fetching project list. Status={}", status);
		return service.list(status);
	}

	// Get project by ID
	@GetMapping("/projects/{id}")
	public ResearchProjectResponse get(@PathVariable Long id) {

		log.info("Fetching project by ID={}", id);
		return service.get(id);
	}

	// Delete project
	@DeleteMapping("/projects/{id}")
	public ResponseEntity<List<String>> delete(@PathVariable Long id) {

		log.warn("DELETE project request for projectId={}", id);
		service.delete(id);

		return ResponseEntity.ok(List.of("Project deleted successfully"));
	}
}
