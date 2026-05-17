package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

	List<Resource> findByProgramId(Long programId);

	List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);

	List<Resource> findByProgramIdAndTypeAndStatus(Long programId, ResourceType type, ResourceStatus status);

	@Query("""
			    SELECT r FROM Resource r
			    WHERE (:type IS NULL OR r.type = :type)
			      AND (:status IS NULL OR r.status = :status)
			""")
	List<Resource> searchResource(@Param("type") ResourceType type, @Param("status") ResourceStatus status);
	
	@Query("""
			    SELECT r FROM Resource r
			    WHERE r.programId = :programId
			      AND (:type IS NULL OR r.type = :type)
			      AND (:status IS NULL OR r.status = :status)
			""")
	List<Resource> searchResourceByProgram(@Param("programId") Long programId, @Param("type") ResourceType type,
			@Param("status") ResourceStatus status);
	
	Long countByType(ResourceType type);

	Long countByTypeAndStatus(ResourceType type, ResourceStatus status);
	
	@Query("SELECT COALESCE(CAST(SUM(r.quantity) AS long), 0L) FROM Resource r WHERE r.type = :type")
	Long sumAmountByType(ResourceType type);
	
	@Query("SELECT COALESCE(CAST(SUM(r.quantity) AS long), 0L) FROM Resource r WHERE r.type = :type and r.status = :status")
	Long sumAmountByTypeAndStatus(ResourceType type,  ResourceStatus status);
	
	List<Resource> findByProgramIdAndTypeAndStatusIn(Long programId, ResourceType type, List<ResourceStatus> statuses);


}