package com.healthgov.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.AuditUpdateRequest;
import com.healthgov.enums.AuditStatus;
import com.healthgov.exceptions.AuditRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
//import com.healthgov.models.Users;
import com.healthgov.models.Audit;
import com.healthgov.repository.AuditRepository;
//import com.healthgov.repository.GrantRepository;
//import com.healthgov.repository.HealthProgramRepository;
//import com.healthgov.repository.ResearchProjectRepository;
//import com.healthgov.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

	private static final Set<String> ALLOWED_SCOPE_TYPES = Set.of("PROGRAM", "PROJECT", "GRANT");

	private final AuditRepository auditRepo;
//	private final UsersRepository usersRepo;
//	private final HealthProgramRepository healthProgramRepo;
//	private final ResearchProjectRepository researchProjectRepo;
//	private final GrantRepository grantsRepo;
	private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

	@Override
	public List<Audit> getAllAudits() {
		return auditRepo.findAll();
	}

	@Override
	public Audit createAudit(AuditCreateRequest request) {

//		Users officer = usersRepo.findByUserIdAndRole(request.getOfficerId(), UserRole.COMPLIANCE)
//				.orElseThrow(() -> new ResourceNotFoundException(
//						"Enter only compliance officer id. Compliance Officer not found: id="
//								+ request.getOfficerId()));

		String scope = request.getScope().trim();
		validateAndEnsureScopeTargetExists(scope);

		// DUPLICATE CHECK (officer + scope)
//		if (auditRepo.existsByOfficer_UserIdAndScopeIgnoreCase(officer.getUserId(), scope)) {
//			throw new AuditRequestException("Duplicate audit: audit already exists for officerId=" + officer.getUserId()
//					+ " and scope=" + scope);
//		}

		LocalDate date = (request.getDate() != null) ? request.getDate() : LocalDate.now();
		if (date.isAfter(LocalDate.now())) {
			throw new AuditRequestException("Audit date cannot be in the future.");
		}

		AuditStatus status = (request.getStatus() != null) ? request.getStatus() : AuditStatus.SCHEDULED;

		if (request.getFindings() == null || request.getFindings().isBlank()) {
			throw new AuditRequestException("findings is required.");
		}

		Audit audit = new Audit();
		audit.setOfficerId(new Long(123)); // get userId
		audit.setScope(scope);
		audit.setFindings(request.getFindings().trim());
		audit.setDate(date);
		audit.setStatus(status);

		return audit;
	}

	@Override
	public Audit updateAudit(Long auditId, AuditUpdateRequest request) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException(" Audit not found: id= " + auditId));

		AuditStatus status = parseStatusOrThrow(request.getStatus().toString());

		existing.setFindings(request.getFindings().trim());
		existing.setDate(request.getDate());
		existing.setStatus(status);

		Audit saved = auditRepo.save(existing);

		log.info("AUDIT_UPDATE", "Audit(auditId=" + saved.getAuditId() + ", status=" + saved.getStatus() + ")");

		return saved;
	}

	@Override
	public Audit updateStatus(Long auditId, String status) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");
		AuditStatus parsed = parseStatusOrThrow(status);

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));

		existing.setStatus(parsed);
		Audit saved = auditRepo.save(existing);

		log.info("AUDIT_UPDATE_STATUS", "Audit(auditId=" + saved.getAuditId() + ", status=" + saved.getStatus() + ")");

		return saved;
	}

	@Override
	public List<Audit> getAllAuditsByOfficer(Long officerId) {

		if (officerId == null)
			throw new AuditRequestException("Officer Id is required.");

		return auditRepo.findByOfficerId(officerId);
	}

	@Override
	public Audit updateFindings(Long auditId, String findings) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");
		if (findings == null || findings.isBlank())
			throw new AuditRequestException("findings is required.");

		Audit existing = auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));

		existing.setFindings(findings.trim());
		Audit saved = auditRepo.save(existing);

		log.info("AUDIT_UPDATE_FINDINGS", "Audit(auditId=" + saved.getAuditId() + ")");

		return saved;
	}

	@Override
	@Transactional(readOnly = true)
	public Audit getAudit(Long auditId) {
		if (auditId == null)
			throw new AuditRequestException("auditId is required.");
		return auditRepo.findById(auditId)
				.orElseThrow(() -> new ResourceNotFoundException("Audit not found: id=" + auditId));
	}

	private AuditStatus parseStatusOrThrow(String status) throws AuditRequestException {
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

	private void validateAndEnsureScopeTargetExists(String scope) throws AuditRequestException {
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

		boolean exists=true;
//		switch (type) {
//		case "PROGRAM" -> exists = healthProgramRepo.existsById(id);
//		case "PROJECT" -> exists = researchProjectRepo.existsById(id);
//		case "GRANT" -> exists = grantsRepo.existsById(id);
//		default -> exists = false;
//		} other service data is 

		if (!exists) {
			throw new ResourceNotFoundException("Scope target not found: " + type + " id=" + id);
		}
	}

}