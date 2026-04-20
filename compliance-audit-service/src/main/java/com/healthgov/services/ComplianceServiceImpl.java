package com.healthgov.services;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceResponseDTO;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.exceptions.ComplianceRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.feignclients.ProgramClient;
import com.healthgov.feignclients.ProjectClient;
import com.healthgov.models.ComplianceRecord;
import com.healthgov.repository.ComplianceRecordRepository;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final ProgramClient programClient;
	private final ProjectClient projectClient;

	private static final Logger log = LoggerFactory.getLogger(ComplianceServiceImpl.class);

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceResponseDTO> getAllComplianceRecords() {
		log.info("Retrived All Compliance Records form the table.");
		return complianceRepo.findAll().stream().map(this::convertToDto).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public ComplianceResponseDTO getOneByEntityIdAndType(ComplianceType type, Long entityId) {

		ComplianceRecord compRecord = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type= " + type + " and entityId=" + entityId));
		log.info("Executed Get Compliance Record By Id function : {}", compRecord);
		return convertToDto(compRecord);
	}

	@Override
	public ComplianceResponseDTO getComplianceById(Long complianceId) {
		ComplianceRecord record = complianceRepo.findById(complianceId).orElseThrow(
				() -> new ResourceNotFoundException("Compliance record not found for ID =" + complianceId));
		return convertToDto(record);

	}

	@Override
	public ComplianceResponseDTO createRecord(ComplianceCreateRequest request) {
		if (request == null)
			throw new ComplianceRequestException("Request body is required.");

		if (complianceRepo.existsByEntityIdAndType(request.getEntityId(), request.getType())) {
			throw new ComplianceRequestException("Compliance record already exists for type=" + request.getType()
					+ " and entityId=" + request.getEntityId());
		}
		
		validateTypeAndEntityId(request.getType(), request.getEntityId());
		ensureTargetExists(request.getType(), request.getEntityId());

		ComplianceRecord compRecord = new ComplianceRecord();
		compRecord.setType(request.getType());
		compRecord.setEntityId(request.getEntityId());
		compRecord.setResult(parseResultOrThrow(request.getResult()));
		compRecord.setDate(normalizeDate(request.getDate()));
		compRecord.setNotes(request.getNotes().trim());

		ComplianceRecord saved = complianceRepo.save(compRecord);

		log.info("Created ComplianceRecord id={} type={} entityId={}", saved.getComplianceId(), saved.getType(),
				saved.getEntityId());

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateExisting(ComplianceType type, Long entityId, ComplianceUpdateRequest dto) {
		validateTypeAndEntityId(type, entityId);
		if (dto == null)
			throw new ComplianceRequestException("Request body is required.");

		ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setResult(parseResultOrThrow(dto.getResult()));
		existing.setDate(normalizeDate(dto.getDate()));
		existing.setNotes(dto.getNotes().trim());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Updated ComplianceRecord id={} type={} entityId={}", saved.getComplianceId(), saved.getType(),
				saved.getEntityId());

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateResultByEntityIdAndType(ComplianceType type, Long entityId, String result) {
		validateTypeAndEntityId(type, entityId);
		ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setResult(parseResultOrThrow(result));
		existing.setDate(LocalDate.now());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Compliance Record Updated with new Result : {}", saved);

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateNotesByEntityIdAndType(ComplianceType type, Long entityId, String notes) {
		validateTypeAndEntityId(type, entityId);
		ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setNotes(notes.trim());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Compliance Notes Updates Successfully.. {}", saved);

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO deleteById(Long id) {
		if (id == null)
			throw new ComplianceRequestException("Compliance Record Not found with Id : " + id);

		ComplianceRecord existing = complianceRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Compliance record not found with ID:" + id));

		complianceRepo.deleteById(id);
		log.info("Deleted Compliance Record form the table : {}", existing);
		return convertToDto(existing);
	}

	private void validateTypeAndEntityId(ComplianceType type, Long entityId) {

		log.info("Validating the compliance type before updatring the status");
		if (type == null)
			throw new ComplianceRequestException("type is required (PROGRAM/PROJECT/GRANT).");
		if (entityId == null)
			throw new ComplianceRequestException("entityId is required.");

		if (!EnumSet.of(ComplianceType.PROGRAM, ComplianceType.PROJECT, ComplianceType.GRANT).contains(type)) {
			throw new ComplianceRequestException("Invalid type. Allowed: PROGRAM, PROJECT, GRANT.");
		}
	}

	private LocalDate normalizeDate(LocalDate date) {
		log.info("Data Validation Invoked");
		LocalDate effective = (date != null) ? date : LocalDate.now();
		if (effective.isAfter(LocalDate.now())) {
			throw new ComplianceRequestException("date cannot be in the future.");
		}
		return effective;
	}

	
	private ComplianceResult parseResultOrThrow(String result) {
		log.info("Validating the compliance result based on the enums");
		try {
			return ComplianceResult.valueOf(result.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new ComplianceRequestException("Invalid result. Allowed: COMPLIANT, PARTIAL, NON_COMPLIANT.");
		}
	}

	private void ensureTargetExists(ComplianceType type, Long entityId) {

		log.info("Validating target entity: type={}, entityId={}", type, entityId);

		if (type == null || entityId == null) {
			throw new ComplianceRequestException("type and entityId are required.");
		}

		Boolean exists;

		try {
			exists = switch (type) {
			case PROGRAM -> programClient.programExists(entityId);
			case PROJECT -> projectClient.projectExists(entityId);
			case GRANT -> projectClient.grantExists(entityId);
			};
		} catch (FeignClientException e) {
			log.error("Feign validation failed for {}:{}", type, entityId, e);
			throw new ComplianceRequestException("Unable to validate target entity. Please try again later.");
		}

		if (exists == null || !exists) {
			throw new ResourceNotFoundException("Target not found: " + type + " id=" + entityId);
		}
	}

	private ComplianceResponseDTO convertToDto(ComplianceRecord rec) {
		ComplianceResponseDTO dto = new ComplianceResponseDTO();
		dto.setComplianceId(rec.getComplianceId());
		dto.setDate(rec.getDate());
		dto.setNotes(rec.getNotes());
		dto.setResult(rec.getResult());
		dto.setEntityId(rec.getEntityId());
		dto.setType(rec.getType());
		return dto;
	}

}