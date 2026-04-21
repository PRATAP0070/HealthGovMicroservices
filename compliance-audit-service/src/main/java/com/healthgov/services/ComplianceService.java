package com.healthgov.services;

import java.util.List;

import com.healthgov.dtos.ComplianceCreateRequest;
import com.healthgov.dtos.ComplianceResponseDTO;
import com.healthgov.dtos.ComplianceUpdateRequest;
import com.healthgov.enums.ComplianceType;

public interface ComplianceService {

	List<ComplianceResponseDTO> getAllComplianceRecords();

	ComplianceResponseDTO getComplianceById(Long complianceId);

	ComplianceResponseDTO getOneByEntityIdAndType(ComplianceType type, Long entityId);

	ComplianceResponseDTO createRecord(ComplianceCreateRequest complianceRecord);

	ComplianceResponseDTO updateExisting(ComplianceType type, Long entityId, ComplianceUpdateRequest dto);

	ComplianceResponseDTO updateResultByEntityIdAndType(ComplianceType type, Long entityId, String result);

	ComplianceResponseDTO updateNotesByEntityIdAndType(ComplianceType type, Long entityId, String notes);

	ComplianceResponseDTO deleteById(Long id);
}