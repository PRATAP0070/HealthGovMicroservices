package com.healthgov.services;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.exceptions.ComplianceRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.feignclients.GrantClient;
import com.healthgov.feignclients.ProgramClient;
import com.healthgov.feignclients.ProjectClient;
import com.healthgov.models.ComplianceRecord;
import com.healthgov.repository.ComplianceRecordRepository;
//import com.healthgov.repository.GrantRepository;
//import com.healthgov.repository.HealthProgramRepository;
//import com.healthgov.repository.ResearchProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
    private final ProgramClient programClient;
    private final ProjectClient projectClient;
    private final GrantClient grantClient;

	private static final Logger log = LoggerFactory.getLogger(ComplianceServiceImpl.class);

	@Override
	@Transactional(readOnly = true)
	public List<ComplianceRecord> getAllComplianceRecords() {
		log.info("Retrived All Compliance Records form the table.");
		return complianceRepo.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public ComplianceRecord getOneByEntityIdAndType(ComplianceType type, Long entityId) {
		validateTypeAndEntityId(type, entityId);
		ensureTargetExists(type, entityId);

		ComplianceRecord record = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type= " + type + " and entityId=" + entityId));
		log.info("Executed Get Compliance Record By Id function : {}", record);
		return record;
	}

	@Override
	public ComplianceRecord createRecord(ComplianceCreateRequest request) {
		if (request == null)
			throw new ComplianceRequestException("Request body is required.");

		validateTypeAndEntityId(request.getType(), request.getEntityId());
		ensureTargetExists(request.getType(), request.getEntityId());

		if (complianceRepo.existsByEntityIdAndType(request.getEntityId(), request.getType())) {
			throw new ComplianceRequestException("Compliance record already exists for type=" + request.getType()
					+ " and entityId=" + request.getEntityId());
		}

		ComplianceRecord compRecord = new ComplianceRecord();
		compRecord.setType(request.getType());
		compRecord.setEntityId(request.getEntityId());
		compRecord.setResult(parseResultOrThrow(request.getResult()));
		compRecord.setDate(normalizeDate(request.getDate()));
		compRecord.setNotes(request.getNotes().trim());

		ComplianceRecord saved = complianceRepo.save(compRecord);

		log.info("Created ComplianceRecord id={} type={} entityId={}", saved.getComplianceId(), saved.getType(),
				saved.getEntityId());

		return saved;
	}

	@Override
	public ComplianceRecord updateExisting(ComplianceType type, Long entityId, ComplianceUpdateRequest dto) {
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

		return saved;
	}

	@Override
	public ComplianceRecord updateResultByEntityIdAndType(ComplianceType type, Long entityId, String result) {
		validateTypeAndEntityId(type, entityId);
		ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setResult(parseResultOrThrow(result));
		existing.setDate(LocalDate.now());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Compliance Record Updated with new Result : {}", saved);

		return saved;
	}

	@Override
	public ComplianceRecord updateNotesByEntityIdAndType(ComplianceType type, Long entityId, String notes) {
		validateTypeAndEntityId(type, entityId);
		ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setNotes(notes.trim());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Compliance Notes Updates Successfully.. {}", saved);

		return saved;
	}

	@Override
	public ComplianceRecord deleteById(Long Id) {
		if (Id == null)
			throw new ComplianceRequestException("Compliance Record Not found with Id : " + Id);

		ComplianceRecord existing = complianceRepo.findById(Id)
				.orElseThrow(() -> new ResourceNotFoundException("Compliance record not found with ID:" + Id));

		complianceRepo.deleteById(Id);
		log.info("Deleted Compliance Record form the table : {}", existing);
		return existing;
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
		log.info("Validating the Entity ID and It's Type pair in respective table");
		boolean exists=true;
//		switch (type) {
//		case PROGRAM -> exists = healthProgramRepo.existsById(entityId);
//		case PROJECT -> exists = researchProjectRepo.existsById(entityId);
//		case GRANT -> exists = grantsRepo.existsById(entityId);
//		default -> exists = false;
//		}
		if (!exists) {
			throw new ResourceNotFoundException("Target not found: " + type + " id=" + entityId);
		}
	}

}