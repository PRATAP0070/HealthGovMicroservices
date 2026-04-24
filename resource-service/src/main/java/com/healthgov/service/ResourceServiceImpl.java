package com.healthgov.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.FundsResourceReportDTO;
import com.healthgov.dto.PhysicalResourceReportDTO;
import com.healthgov.dto.ResourceCreateRequest;
import com.healthgov.dto.ResourceReportResponseDTO;
import com.healthgov.dto.ResourceResponse;
import com.healthgov.dto.ResourceUpdateRequest;
import com.healthgov.enums.ProgramStatus;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.external.ProgramFeignClient;
import com.healthgov.external.dto.ProgramStatusResponse;
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

	public ResourceServiceImpl(ResourceRepository resourceRepo, ProgramFeignClient programFeignClient) {
		this.resourceRepo = resourceRepo;
		this.programFeignClient = programFeignClient;
	}

	@Override
	public ResourceResponse createResource(ResourceCreateRequest request) {

		log.info("Creating resource for programId={}", request.getProgramId());

		// Validate program
		ProgramStatusResponse program = programFeignClient.getProgramStatus(request.getProgramId());

		if (program.getStatus() != ProgramStatus.ACTIVE) {
			throw new IllegalStateException("Cannot create resource for program with status: " + program.getStatus());
		}

		validateResourceRequest(request.getQuantity(), request.getType(), request.getStatus());

		// Build entity with proposed values (NOT persisted yet)
		Resource entity = new Resource();
		entity.setProgramId(request.getProgramId());
		entity.setType(request.getType());
		entity.setQuantity(request.getQuantity());

		// TYPE-BASED BUSINESS RULES

		if (request.getType() == ResourceType.FUNDS) {

			double allocated = getTotalAllocatedFunds(request.getProgramId());
			double budget = program.getBudget();
			double requested = request.getQuantity();

			if (requested <= (budget - allocated)) {
				entity.setStatus(ResourceStatus.ALLOCATED);
			} else {
				entity.setStatus(ResourceStatus.PENDING);
			}

		} else {
			entity.setStatus(request.getStatus());
		}

		//  Persist only after all validations
		entity = resourceRepo.save(entity);

		log.info("Resource created successfully with resourceId={}", entity.getResourceId());

		return toResponse(entity);
	}

	@Override
	public ResourceResponse updateResource(Long resourceId, ResourceUpdateRequest request) {

		log.info("Updating resource with resourceId={}", resourceId);

		// Load existing resource
		Resource entity = getResourceOrThrow(resourceId);
		
		// Validate program
		ProgramStatusResponse program = programFeignClient.getProgramStatus(entity.getProgramId());

		// Program must be ACTIVE
		if (program.getStatus() != ProgramStatus.ACTIVE) {
			throw new IllegalStateException("Cannot update resource for program with status: " + program.getStatus());
		}
		// Completed resources are immutable
		if (entity.getStatus() == ResourceStatus.COMPLETED) {
			throw new IllegalStateException("Completed resource cannot be modified");
		}

		// Only ACTIVE → COMPLETED allowed
		if (request.getStatus() == ResourceStatus.COMPLETED && entity.getStatus() != ResourceStatus.ACTIVE) {
			throw new IllegalStateException("Only ACTIVE resources can be completed");
		}

		validateResourceRequest(request.getQuantity(), request.getType(), request.getStatus());
		
		ResourceType newType = request.getType();

		/* ---------- FUNDS BUDGET VALIDATION ---------- */
		if (newType == ResourceType.FUNDS) {

			double budget = program.getBudget();
			double allocated = getTotalAllocatedFunds(entity.getProgramId());

			// Remove old allocation only if existing type was FUNDS
			double oldQuantity = entity.getType() == ResourceType.FUNDS ? entity.getQuantity() : 0;

			double newQuantity = request.getQuantity();

			double effectiveAllocated = allocated - oldQuantity + newQuantity;

			if (effectiveAllocated > budget) {
				throw new IllegalStateException(
						"Insufficient budget. Remaining: " + (budget - (allocated - oldQuantity)));
			}	
		}
			
		log.info("Updating resourceId={}, oldStatus={}, newStatus={}", resourceId, entity.getStatus(), request.getStatus());
		
		// Apply update after all validations
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

		if (entity.getStatus() == ResourceStatus.ACTIVE || entity.getStatus() == ResourceStatus.ALLOCATED || entity.getStatus() == ResourceStatus.COMPLETED) {
			throw new IllegalStateException("Cannot delete active or allocated or completed resource");
		}
		// ACTIVE resources are in use; deleting them would cause data loss.
		// COMPLETED resources are historical records; deleting them breaks auditability.
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

	private double getTotalAllocatedFunds(Long programId) {
		return resourceRepo.findByProgramIdAndTypeAndStatus(programId, ResourceType.FUNDS, ResourceStatus.ALLOCATED)
				.stream().mapToDouble(Resource::getQuantity).sum();
	}

	private void validateResourceRequest(int quantity, ResourceType type, ResourceStatus status) {

		if (quantity < 0) {
			throw new IllegalArgumentException("Resource quantity cannot be negative");
		}

		if (type != ResourceType.FUNDS && status == ResourceStatus.PENDING) {
			throw new IllegalStateException("PENDING is only allowed for FUNDS");
		}

		if (type == ResourceType.FUNDS && status == ResourceStatus.INACTIVE) {
			throw new IllegalStateException("FUNDS cannot be INACTIVE");
		}
	}
	

	public ResourceReportResponseDTO generateResourceReport() {

		FundsResourceReportDTO fundsReport = buildFundsResourceReport();

		List<PhysicalResourceReportDTO> physicalResourcesReport = List.of(buildPhysicalResourceReport(ResourceType.LAB),
				buildPhysicalResourceReport(ResourceType.EQUIPMENT));

		return new ResourceReportResponseDTO(fundsReport, physicalResourcesReport);
	}
	
	private FundsResourceReportDTO buildFundsResourceReport() {

		return new FundsResourceReportDTO(
				resourceRepo.sumAmountByTypeAndStatus(ResourceType.FUNDS, ResourceStatus.PENDING),
				resourceRepo.sumAmountByTypeAndStatus(ResourceType.FUNDS, ResourceStatus.ALLOCATED),
				resourceRepo.sumAmountByTypeAndStatus(ResourceType.FUNDS, ResourceStatus.ACTIVE),
				resourceRepo.sumAmountByTypeAndStatus(ResourceType.FUNDS, ResourceStatus.COMPLETED),
				resourceRepo.sumAmountByType(ResourceType.FUNDS));
	}

	private PhysicalResourceReportDTO buildPhysicalResourceReport(ResourceType type) {

		return new PhysicalResourceReportDTO(type, resourceRepo.countByTypeAndStatus(type, ResourceStatus.ALLOCATED),
				resourceRepo.countByTypeAndStatus(type, ResourceStatus.ACTIVE),
				resourceRepo.countByTypeAndStatus(type, ResourceStatus.INACTIVE),
				resourceRepo.countByTypeAndStatus(type, ResourceStatus.COMPLETED), resourceRepo.countByType(type));
	}
}
