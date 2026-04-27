package com.healthgov.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceResponseDTO;
import com.healthgov.dtos.ComplianceSummaryResponseDTO;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.dtos.OfficerComplianceUpdateRequest;
import com.healthgov.dtos.UserResponseDto;
import com.healthgov.enums.AuditStatus;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.exceptions.ComplianceRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.models.ComplianceRecord;
import com.healthgov.repository.ComplianceRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

	private final ComplianceRecordRepository complianceRepo;
	private final ComplianceUtil complianceUtil;
	private final AuditService auditService;

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

		complianceUtil.validateTypeAndEntityId(request.getType(), request.getEntityId());
		complianceUtil.ensureTargetExists(request.getType(), request.getEntityId());

		ComplianceRecord compRecord = new ComplianceRecord();
		compRecord.setType(request.getType());
		compRecord.setEntityId(request.getEntityId());
		compRecord.setResult(complianceUtil.parseResultOrThrow(request.getResult()));
		compRecord.setDate(normalizeDate(request.getDate()));
		compRecord.setNotes(request.getNotes().trim());

		ComplianceRecord saved = complianceRepo.save(compRecord);

		log.info("Created ComplianceRecord id={} type={} entityId={}", saved.getComplianceId(), saved.getType(),
				saved.getEntityId());

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateExisting(ComplianceType type, Long entityId, ComplianceUpdateRequest dto) {
		complianceUtil.validateTypeAndEntityId(type, entityId);
		if (dto == null)
			throw new ComplianceRequestException("Request body is required.");

		complianceUtil.ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setResult(complianceUtil.parseResultOrThrow(dto.getResult()));
		existing.setDate(normalizeDate(dto.getDate()));
		existing.setNotes(dto.getNotes().trim());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Updated ComplianceRecord id={} type={} entityId={}", saved.getComplianceId(), saved.getType(),
				saved.getEntityId());

		return convertToDto(saved);
	}

	@Override
	@Transactional
	public ComplianceResponseDTO updateByOfficer(ComplianceType type, Long entityId,
			OfficerComplianceUpdateRequest request) {

		//Centralized officer validation
		UserResponseDto officer = complianceUtil.validateComplianceOfficer(request.getOfficerId());

		ComplianceRecord record = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + ", entityId=" + entityId));

		//Officer is allowed to change ONLY these
		record.setResult(request.getResult());

		if (request.getNotes() != null && !request.getNotes().isBlank()) {
			record.setNotes(request.getNotes());
		}

		record.setDate(LocalDate.now());

		ComplianceRecord saved = complianceRepo.save(record);

		AuditCreateRequest auditRequest = new AuditCreateRequest();
		auditRequest.setDate(LocalDate.now());
		auditRequest.setOfficerId(officer.getUserId());
		auditRequest.setStatus(AuditStatus.SCHEDULED);
		auditRequest.setFindings("Audit Created by the Officer " + officer.getUserId() + " for Compliance ID: "
				+ saved.getComplianceId());
		auditRequest.setScope(saved.getType() + ":" + saved.getEntityId());

		auditService.createAudit(auditRequest);

		log.info("Compliance record id={} updated by officerId={} officerName={}", saved.getComplianceId(),
				officer.getUserId(), officer.getName());

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateResultByEntityIdAndType(ComplianceType type, Long entityId, String result) {
		complianceUtil.validateTypeAndEntityId(type, entityId);
		complianceUtil.ensureTargetExists(type, entityId);

		ComplianceRecord existing = complianceRepo.findOneByEntityIdAndType(entityId, type)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Compliance record not found for type=" + type + " and entityId=" + entityId));

		existing.setResult(complianceUtil.parseResultOrThrow(result));
		existing.setDate(LocalDate.now());

		ComplianceRecord saved = complianceRepo.save(existing);

		log.info("Compliance Record Updated with new Result : {}", saved);

		return convertToDto(saved);
	}

	@Override
	public ComplianceResponseDTO updateNotesByEntityIdAndType(ComplianceType type, Long entityId, String notes) {
		complianceUtil.validateTypeAndEntityId(type, entityId);
		complianceUtil.ensureTargetExists(type, entityId);

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

	@Transactional(readOnly = true)
	public ComplianceSummaryResponseDTO getComplianceSummary() {

		long total = complianceRepo.count();

		Map<ComplianceType, Long> byType = complianceRepo.countByType().stream()
				.collect(Collectors.toMap(r -> (ComplianceType) r[0], r -> (Long) r[1]));

		Map<ComplianceResult, Long> byResult = complianceRepo.countByResult().stream()
				.collect(Collectors.toMap(r -> (ComplianceResult) r[0], r -> (Long) r[1]));

		log.info("Compliance summary fetched: total={}", total);

		return new ComplianceSummaryResponseDTO(total, byResult, byType);
	}

	private ComplianceResponseDTO convertToDto(ComplianceRecord rec) {
		ComplianceResponseDTO dto = new ComplianceResponseDTO();
		dto.setComplianceId(rec.getComplianceId());
		dto.setDate(rec.getDate());
		dto.setNotes(rec.getNotes());
		dto.setResult(rec.getResult());
		dto.setEntityId(rec.getEntityId());
		dto.setType(rec.getType());

		//Fetch and embed entity details
		dto.setEntity(complianceUtil.fetchEntityDetails(rec.getType(), rec.getEntityId()));

		return dto;
	}

	private LocalDate normalizeDate(LocalDate date) {
		log.info("Data Validation Invoked");
		LocalDate effective = (date != null) ? date : LocalDate.now();
		if (effective.isAfter(LocalDate.now())) {
			throw new ComplianceRequestException("date cannot be in the future.");
		}
		return effective;
	}

}