package com.healthgov.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthgov.dto.ResourceCreateRequest;
import com.healthgov.dto.ResourceResponse;
import com.healthgov.dto.ResourceSummaryResponse;
import com.healthgov.dto.ResourceUpdateRequest;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.service.ResourceService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/resources")
@Slf4j
public class ResourceController {

	private final ResourceService service;

	public ResourceController(ResourceService service) {
		this.service = service;
	}

	@PostMapping("/save")
	public ResourceResponse create(@Valid @RequestBody ResourceCreateRequest request) {
		log.info("Creating new resource");
		return service.createResource(request);
	}

	@PutMapping("/update/{resourceId}")
	public ResourceResponse update(@PathVariable Long resourceId, @Valid @RequestBody ResourceUpdateRequest request) {
		log.info("Updating resource with id={}", resourceId);
		return service.updateResource(resourceId, request);
	}

	@DeleteMapping("/delete/{resourceId}")
	public String delete(@PathVariable Long resourceId) {
		log.info("Deleting resource with id={}", resourceId);
		service.deleteResourceById(resourceId);
		return "Resource deleted successfully";
	}

	@GetMapping("/getById/{resourceId}")
	public ResourceResponse getById(@PathVariable Long resourceId) {
		log.info("Fetching resource by id={}", resourceId);
		return service.getResourceById(resourceId);
	}

	@GetMapping("/getAll")
	public List<ResourceResponse> getAll() {
		log.info("Fetching all resources");
		return service.getAllResources();
	}

	@GetMapping("/program/{programId}")
	public List<ResourceResponse> getByProgram(@PathVariable Long programId) {
		log.info("Fetching resources for programId={}", programId);
		return service.getResourcesByProgramId(programId);
	}

	@GetMapping("/search")
	public List<ResourceResponse> search(@RequestParam ResourceType type, @RequestParam ResourceStatus status) {
		log.info("Searching resources with type={} and status={}", type, status);
		return service.getResourcesByTypeAndStatus(type, status);
	}
	
	@GetMapping("/program/{programId}/summary")
	public ResourceSummaryResponse resourceSummary(@PathVariable Long programId) {
	    return service.getResourceSummary(programId);
	}
}