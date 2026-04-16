package com.healthgov.services;

import java.util.List;

import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceType;
import com.healthgov.models.ComplianceRecord;

public interface ComplianceService {

	List<ComplianceRecord> getAllComplianceRecords();

	ComplianceRecord getOneByEntityIdAndType(ComplianceType type, Long entityId);

	ComplianceRecord createRecord(ComplianceCreateRequest complianceRecord);

	ComplianceRecord updateExisting(ComplianceType type, Long entityId, ComplianceUpdateRequest dto);

	ComplianceRecord updateResultByEntityIdAndType(ComplianceType type, Long entityId, String result);

	ComplianceRecord updateNotesByEntityIdAndType(ComplianceType type, Long entityId, String notes);

	ComplianceRecord deleteById(Long Id);
}