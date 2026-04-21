package com.healthgov.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.InfrastructureCreateRequest;
import com.healthgov.dto.InfrastructureResponse;
import com.healthgov.dto.InfrastructureSummaryResponse;
import com.healthgov.dto.InfrastructureUpdateRequest;
import com.healthgov.dto.StatusCapacitySummary;
import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.enums.ProgramStatus;
import com.healthgov.exceptions.InfrastructureNotFoundException;
import com.healthgov.external.ProgramFeignClient;
import com.healthgov.external.dto.ProgramStatusResponse;
import com.healthgov.model.Infrastructure;
import com.healthgov.repository.InfrastructureRepository;
import com.healthgov.repository.projection.StatusCountProjection;
import com.healthgov.repository.projection.TypeCountProjection;
import com.healthgov.repository.projection.TypeStatusCapacityProjection;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional // All operations run inside a transaction by default
@Slf4j // Logger for tracking flow and issues
public class InfrastructureServiceImpl implements InfrastructureService {

	private final InfrastructureRepository infraRepo;
	private final ProgramFeignClient programFeignClient;

	// Constructor injection for repositories
	public InfrastructureServiceImpl(InfrastructureRepository infraRepo, ProgramFeignClient programFeignClient) {
		this.infraRepo = infraRepo;
		this.programFeignClient = programFeignClient;
	}

	@Override
	public InfrastructureResponse createInfrastructure(InfrastructureCreateRequest request) {

		log.info("Creating infrastructure for programId={}", request.getProgramId());
		// First, make sure the HealthProgram exists

		ProgramStatusResponse program = programFeignClient.getProgramStatus(request.getProgramId());

		if (program.getStatus() != ProgramStatus.ACTIVE) {
			throw new IllegalStateException(
					"Cannot create infrastructure for program with status: " + program.getStatus());
		}

		if (request.getCapacity() < 0) {
			throw new IllegalArgumentException("Infrastructure capacity cannot be negative");
		}

		// Build and save entity
		Infrastructure entity = new Infrastructure();
		entity.setProgramId(request.getProgramId());
		entity.setType(request.getType());
		entity.setLocation(request.getLocation());
		entity.setCapacity(request.getCapacity());
		entity.setStatus(request.getStatus());

		// Save infrastructure in DB
		entity = infraRepo.save(entity);

		log.info("Infrastructure created successfully with infraId={}", entity.getInfraId());

		// Convert entity to response DTO before returning
		return toResponse(entity);
	}

	@Override
	public InfrastructureResponse updateInfrastructure(Long infraId, InfrastructureUpdateRequest request) {

		log.info("Updating infrastructure with infraId={}", infraId);
		// Load existing infrastructure or throw exception
		Infrastructure entity = getInfrastructureOrThrow(infraId);

		ProgramStatusResponse program = programFeignClient.getProgramStatus(entity.getProgramId());

		if (program.getStatus() != ProgramStatus.ACTIVE) {
			throw new IllegalStateException(
					"Cannot update infrastructure because program is status: " + program.getStatus());
		}
		if (entity.getStatus() == InfrastructureStatus.DECOMMISSIONED) {
			throw new IllegalStateException("Decommissioned infrastructure cannot be modified");
		}
		if (request.getCapacity() < 0) {
			throw new IllegalArgumentException("Infrastructure capacity cannot be negative");
		}

		entity.setType(request.getType());
		entity.setLocation(request.getLocation());
		entity.setCapacity(request.getCapacity());
		entity.setStatus(request.getStatus());

		infraRepo.save(entity);
		log.info("Infrastructure updated successfully with infraId={}", infraId);
		return toResponse(entity);
	}

	@Override
	public void deleteInfrastructureById(Long infraId) {

		log.info("Deleting infrastructure with infraId={}", infraId);
		// Ensure infrastructure exists before deletion
		Infrastructure entity = getInfrastructureOrThrow(infraId);

		if (entity.getStatus() == InfrastructureStatus.OPERATIONAL || entity.getStatus() == InfrastructureStatus.DECOMMISSIONED) {
			throw new IllegalStateException("OPERATIONAL or DECOMMISSIONED infrastructure cannot be deleted");
		}

		infraRepo.delete(entity);
		log.info("Infrastructure deleted successfully with infraId={}", infraId);

	}

	@Override
	@Transactional(readOnly = true) // Optimized for read-only access
	public InfrastructureResponse getInfrastructureById(Long infraId) {

		log.info("Fetching infrastructure with infraId={}", infraId);

		Infrastructure entity = getInfrastructureOrThrow(infraId);
		return toResponse(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponse> getAllInfrastructures() {
		log.info("Fetching all infrastructures");
		return infraRepo.findAll().stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponse> getInfrastructuresByProgramId(Long programId) {
		log.info("Fetching infrastructures for programId={}", programId);
		return infraRepo.findByProgramId(programId).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfrastructureResponse> getInfrastructuresByTypeLocationAndStatus(InfrastructureType type,
			String location, InfrastructureStatus status) {

		log.info("Searching infrastructures with type={}, location={}, status={}", type, location, status);

		return infraRepo.findByTypeAndLocationAndStatus(type, location, status).stream().map(this::toResponse).toList();
	}

	// Common method to load Infrastructure or throw exception
	private Infrastructure getInfrastructureOrThrow(Long infraId) {
		log.debug("Loading infrastructure with infraId={}", infraId);

		return infraRepo.findById(infraId).orElseThrow(() -> {
			log.error("Infrastructure not found with infraId={}", infraId);
			return new InfrastructureNotFoundException("Infrastructure not found: " + infraId);
		});
	}

	// Maps Infrastructure entity to response DTO
	private InfrastructureResponse toResponse(Infrastructure e) {
		InfrastructureResponse dto = new InfrastructureResponse();
		dto.setInfraId(e.getInfraId());
		dto.setProgramId(e.getProgramId());
		dto.setType(e.getType());
		dto.setLocation(e.getLocation());
		dto.setCapacity(e.getCapacity());
		dto.setStatus(e.getStatus());
		return dto;
	}

	@Override
	@Transactional(readOnly = true)
	public InfrastructureSummaryResponse getSummaryByProgramId(Long programId) {

		InfrastructureSummaryResponse response = new InfrastructureSummaryResponse();
		response.setProgramId(programId);

		// 1️ Total capacity
		response.setTotalCapacity(infraRepo.sumCapacityByProgramId(programId));

		// 2️ Count by status
		response.setCountByStatus(infraRepo.countByStatus(programId).stream()
				.collect(Collectors.toMap(StatusCountProjection::getStatus, StatusCountProjection::getCount)));

		// 3️ Count by type
		response.setCountByType(infraRepo.countByType(programId).stream()
				.collect(Collectors.toMap(TypeCountProjection::getType, TypeCountProjection::getCount)));

		// 4️ Type → Status → Capacity summary
		Map<InfrastructureType, Map<InfrastructureStatus, StatusCapacitySummary>> typeStatusSummary = new HashMap<>();

		for (TypeStatusCapacityProjection row : infraRepo.aggregateByTypeAndStatus(programId)) {

			typeStatusSummary.computeIfAbsent(row.getType(), t -> new HashMap<>()).put(row.getStatus(),
					new StatusCapacitySummary(row.getCount(), row.getTotalCapacity()));
		}

		response.setTypeStatusSummary(typeStatusSummary);

		return response;
	}

}