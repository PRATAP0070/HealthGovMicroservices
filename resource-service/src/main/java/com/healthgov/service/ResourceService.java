package com.healthgov.service;

import java.util.List;

import com.healthgov.dto.ResourceCreateRequest;
import com.healthgov.dto.ResourceResponse;
import com.healthgov.dto.ResourceUpdateRequest;
import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;

public interface ResourceService {

	ResourceResponse createResource(ResourceCreateRequest request);

	ResourceResponse updateResource(Long resourceId, ResourceUpdateRequest request);

	void deleteResourceById(Long resourceId);

	ResourceResponse getResourceById(Long resourceId);

	List<ResourceResponse> getAllResources();

	List<ResourceResponse> getResourcesByProgramId(Long programId);

	List<ResourceResponse> getResourcesByTypeAndStatus(ResourceType type, ResourceStatus status);
}