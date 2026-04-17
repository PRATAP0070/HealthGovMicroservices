package com.healthgov.services;

import java.util.List;

import com.healthgov.dtos.AuditCreateRequest;
import com.healthgov.dtos.AuditUpdateRequest;
import com.healthgov.models.Audit;

public interface AuditService {

	List<Audit> getAllAudits();

	Audit createAudit(AuditCreateRequest request);

	Audit updateAudit(Long auditId, AuditUpdateRequest request);

	Audit updateStatus(Long auditId, String status);

	Audit updateFindings(Long auditId, String findings);

	Audit getAudit(Long auditId);

	List<Audit> getAllAuditsByOfficer(Long officerId);
}