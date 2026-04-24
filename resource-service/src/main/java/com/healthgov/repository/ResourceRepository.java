package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

	List<Resource> findByProgramId(Long programId);

	List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);

	List<Resource> findByProgramIdAndTypeAndStatus(Long programId, ResourceType type, ResourceStatus status);
	
	Long countByType(ResourceType type);

	Long countByTypeAndStatus(ResourceType type, ResourceStatus status);
	
	@Query("SELECT COALESCE(CAST(SUM(r.amount) AS long), 0L) FROM Resource r WHERE r.type = :type")
	Long sumAmountByType(ResourceType type);


}