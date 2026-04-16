package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

	List<Resource> findByProgramId(Long programId);

	List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);
}