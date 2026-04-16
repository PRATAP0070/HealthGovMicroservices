package com.healthgov.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.ResourceCreateRequest;
import com.healthgov.dto.ResourceResponse;
import com.healthgov.dto.ResourceUpdateRequest;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.external.ProgramFeignClient;
import com.healthgov.model.Resource;
import com.healthgov.repository.ResourceRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class ResourceServiceImpl implements ResourceService {

	// Handles DB operations related to Resource entity
	private final ResourceRepository resourceRepo;
	private final ProgramFeignClient programFeignClient;
	public ResourceServiceImpl(ResourceRepository resourceRepo,ProgramFeignClient programFeignClient) {
		this.resourceRepo = resourceRepo;
		this.programFeignClient = programFeignClient;
	}

	@Override
	public ResourceResponse createResource(ResourceCreateRequest request) {

		log.info("Creating resource for programId={}", request.getProgramId());

		programFeignClient.validateProgramExists(request.getProgramId());
		// Build and save entity
		Resource entity = new Resource();
		entity.setProgramId(request.getProgramId());
		entity.setType(request.getType());
		entity.setQuantity(request.getQuantity());
		entity.setStatus(request.getStatus());
		// Persist resource to database
		entity = resourceRepo.save(entity);

		log.info("Resource created successfully with resourceId={}", entity.getResourceId());

		// Map to response
		return toResponse(entity);
	}

	@Override
	public ResourceResponse updateResource(Long resourceId, ResourceUpdateRequest request) {

		log.info("Updating resource with resourceId={}", resourceId);
		// Load existing resource or fail if not found
		Resource entity = getResourceOrThrow(resourceId);

		entity.setType(request.getType());
		entity.setQuantity(request.getQuantity());
		entity.setStatus(request.getStatus());

		resourceRepo.save(entity);
		log.info("Resource updated successfully with resourceId={}", resourceId);
		return toResponse(entity);
	}

	@Override
	public void deleteResourceById(Long resourceId) {

		log.info("Deleting resource with resourceId={}", resourceId);
		// Confirm resource exists before deleting
		Resource entity = getResourceOrThrow(resourceId);
		resourceRepo.delete(entity);

		log.info("Resource deleted successfully with resourceId={}", resourceId);

	}

	@Override
	@Transactional(readOnly = true)
	// Read-only transaction since this method only fetches data
	public ResourceResponse getResourceById(Long resourceId) {

		log.info("Fetching resource with resourceId={}", resourceId);

		Resource entity = getResourceOrThrow(resourceId);
		return toResponse(entity);

	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponse> getAllResources() {
		log.info("Fetching all resources");
		// Convert list of entities to response DTOs
		return resourceRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponse> getResourcesByProgramId(Long programId) {
		log.info("Fetching resources for programId={}", programId);
		return resourceRepo.findByProgramId(programId).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceResponse> getResourcesByTypeAndStatus(ResourceType type, ResourceStatus status) {
		log.info("Searching resources with type={} and status={}", type, status);
		return resourceRepo.findByTypeAndStatus(type, status).stream().map(this::toResponse).toList();
	}

	// Common helper to fetch Resource or throw exception if missing
	private Resource getResourceOrThrow(Long resourceId) {

		log.debug("Loading resource with resourceId={}", resourceId);

		return resourceRepo.findById(resourceId).orElseThrow(() -> {
			log.error("Resource not found with resourceId={}", resourceId);
			return new ResourceNotFoundException("Resource not found: " + resourceId);
		});
	}

	// Converts Resource entity into API response object
	private ResourceResponse toResponse(Resource e) {
		ResourceResponse dto = new ResourceResponse();
		dto.setResourceId(e.getResourceId());
		dto.setProgramId(e.getProgramId());
		dto.setType(e.getType());
		dto.setQuantity(e.getQuantity());
		dto.setStatus(e.getStatus());
		return dto;
	}
}
