package com.healthgov.services;

import java.util.List;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.AuditReponseDTO;
import com.healthgov.dtos.AuditUpdateRequest;

public interface AuditService {

	List<AuditReponseDTO> getAllAudits();

	AuditReponseDTO createAudit(AuditCreateRequest request);

	AuditReponseDTO updateAudit(Long auditId, AuditUpdateRequest request);

	AuditReponseDTO updateStatus(Long auditId, String status);

	AuditReponseDTO updateFindings(Long auditId, String findings);

	AuditReponseDTO getAudit(Long auditId);

	List<AuditReponseDTO> getAllAuditsByOfficer(Long officerId);
}