package com.healthgov.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.AuditReponseDTO;
import com.healthgov.dtos.AuditUpdateRequest;
import com.healthgov.enums.AuditStatus;
import com.healthgov.exceptions.AuditRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.feignclients.ProgramClient;
import com.healthgov.feignclients.ProjectClient;
import com.healthgov.feignclients.UserClient;
import com.healthgov.models.Audit;
import com.healthgov.repository.AuditRepository;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

	private static final Set<String> ALLOWED_SCOPE_TYPES = Set.of("PROGRAM", "PROJECT", "GRANT");

	private final AuditRepository auditRepo;
	private final ProgramClient programClient;
	private final ProjectClient projectClient;
	private final UserClient userClient;

	private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

	@Override
	public List<AuditReponseDTO> getAllAudits() {
		return auditRepo.findAll().stream().map(this::convertToDto).toList();
	}

	@Override
	public AuditReponseDTO createAudit(AuditCreateRequest request) {

		Long officerId = request.getOfficerId();

		if (officerId == null) {
			throw new AuditRequestException("Compliance officer ID is required.");
		}

		Boolean exists;
		Boolean isCompliance;

		try {
			isCompliance = userClient.userHasRole(officerId, "COMPLIANCE");
			log.info("Response from the User-Client {}", isCompliance);
		} catch (FeignClientException e) {
			throw new AuditRequestException("Unable to validate compliance officer. User service unavailable.");
		}

		if (isCompliance == null || !isCompliance) {
			throw new AuditRequestException("User is not a COMPLIANCE officer: id=" + officerId);
		}

		String scope = request.getScope().trim();
		validateAndEnsureScopeTargetExists(scope);

		LocalDate date = (request.getDate() != null) ? request.getDate() : LocalDate.now();

		if (date.isAfter(LocalDate.now())) {
			throw new AuditRequestException("Audit date cannot be in the future.");
		}

		Audit audit = new Audit();
		audit.setOfficerId(officerId);
		audit.setScope(scope);
		audit.setFindings(request.getFindings().trim());
		audit.setDate(date);
		audit.setStatus(request.getStatus() != null ? request.getStatus() : AuditStatus.SCHEDULED);

		Audit saved = auditRepo.save(audit);
		return convertToDto(saved);
	}

	@Override
	public AuditReponseDTO updateAudit(Long auditId, AuditUpdateRequest request) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException(" Audit not found: id= " + auditId));

		AuditStatus status = parseStatusOrThrow(request.getStatus().toString());

		existing.setFindings(request.getFindings().trim());
		existing.setDate(request.getDate());
		existing.setStatus(status);

		Audit saved = auditRepo.save(existing);

		log.info("Audit Record updated Successfully {}", saved);

		return convertToDto(saved);
	}

	@Override
	public AuditReponseDTO updateStatus(Long auditId, String status) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");
		AuditStatus parsed = parseStatusOrThrow(status);

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));

		existing.setStatus(parsed);
		Audit saved = auditRepo.save(existing);

		log.info("AUDIT Status updated Successfully. {}", saved);

		return convertToDto(saved);
	}

	@Override
	public List<AuditReponseDTO> getAllAuditsByOfficer(Long officerId) {

		if (officerId == null)
			throw new AuditRequestException("Officer Id is required.");

		return auditRepo.findByOfficerId(officerId).stream().map(this::convertToDto).toList();
	}

	@Override
	public AuditReponseDTO updateFindings(Long auditId, String findings) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");
		if (findings == null || findings.isBlank())
			throw new AuditRequestException("findings is required.");

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));

		existing.setFindings(findings.trim());
		Audit saved = auditRepo.save(existing);

		log.info("AUDIT findings updated Successfully {}", saved);

		return convertToDto(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public AuditReponseDTO getAudit(Long auditId) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");

		log.info("Found Auidt in the Database with ID {}", auditId);
		Audit audit = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));
		return convertToDto(audit);
	}

	private AuditStatus parseStatusOrThrow(String status) {
		if (status == null || status.isBlank()) {
			throw new AuditRequestException(
					"status is required. Allowed: SCHEDULED, IN_REVIEW, COMPLETED, FOLLOW_UP_REQUIRED.");
		}
		try {
			return AuditStatus.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new AuditRequestException(
					"Invalid status. Allowed: SCHEDULED, IN_REVIEW, COMPLETED, FOLLOW_UP_REQUIRED.");
		}
	}

	private void validateAndEnsureScopeTargetExists(String scope) {

		String[] parts = scope.split(":", 2);
		if (parts.length != 2) {
			throw new AuditRequestException("Invalid scope format. Use PROGRAM:<id>, PROJECT:<id>, or GRANT:<id>.");
		}

		String type = parts[0].trim().toUpperCase();
		String idPart = parts[1].trim();

		if (!ALLOWED_SCOPE_TYPES.contains(type)) {
			throw new AuditRequestException("Invalid scope type. Allowed: PROGRAM, PROJECT, GRANT.");
		}

		long id;
		try {
			id = Long.parseLong(idPart);
		} catch (NumberFormatException ex) {
			throw new AuditRequestException("Invalid scope id. Use numeric id. Example: PROGRAM:4");
		}

		Boolean exists;
		try {

			switch (type) {

			case "PROGRAM" -> {
				log.debug("Calling ProgramClient.programExists(id={})", id);
				exists = programClient.programExists(id);
				log.debug("ProgramClient response for id {}: {}", id, exists);
			}

			case "PROJECT" -> {
				log.debug("Calling ProjectClient.projectExists(id={})", id);
				exists = projectClient.projectExists(id);
				log.debug("ProjectClient response for id {}: {}", id, exists);
			}

			case "GRANT" -> {
				log.debug("Calling ProjectClient.grantExists(id={})", id);
				exists = projectClient.grantExists(id);
				log.debug("Grant existence response for id {}: {}", id, exists);
			}

			default -> {
				log.error("Unexpected scope type encountered: {}", type);
				exists = false;
			}
			}
		} catch (FeignClientException e) {
			throw new AuditRequestException("Unable to validate scope. Dependent service unavailable.");
		}

		if (exists == null || !exists) {
			throw new ResourceNotFoundException("Scope target not found: " + type + " id=" + id);
		}
	}

	private AuditReponseDTO convertToDto(Audit a) {
		AuditReponseDTO dto = new AuditReponseDTO();
		dto.setAuditId(a.getAuditId());
		dto.setScope(a.getScope());
		dto.setDate(a.getDate());
		dto.setFindings(a.getFindings());
		dto.setStatus(a.getStatus());
		// dto.setOfficer(a.getOfficer());

		return dto;

	}

}