package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.InfrastructureCreateRequest;
import com.healthgov.dto.InfrastructureResponse;
import com.healthgov.dto.InfrastructureUpdateRequest;
import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;

public interface InfrastructureService {

	InfrastructureResponse createInfrastructure(InfrastructureCreateRequest request);

	InfrastructureResponse updateInfrastructure(Long infraId, InfrastructureUpdateRequest request);

	void deleteInfrastructureById(Long infraId);

	InfrastructureResponse getInfrastructureById(Long infraId);

	List<InfrastructureResponse> getAllInfrastructures();

	List<InfrastructureResponse> getInfrastructuresByProgramId(Long programId);

	List<InfrastructureResponse> getInfrastructuresByTypeLocationAndStatus(InfrastructureType type, String location,
			InfrastructureStatus status);
}