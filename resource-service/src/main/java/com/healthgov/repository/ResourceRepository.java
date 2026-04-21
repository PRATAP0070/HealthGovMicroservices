package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.healthgov.enums.ResourceStatus;
import com.healthgov.enums.ResourceType;
import com.healthgov.model.Resource;
import com.healthgov.repository.projection.FundAmountByStatusProjection;
import com.healthgov.repository.projection.PhysicalQuantityProjection;
import com.healthgov.repository.projection.ResourceStatusCountProjection;
import com.healthgov.repository.projection.ResourceTypeCountProjection;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

	List<Resource> findByProgramId(Long programId);

	List<Resource> findByTypeAndStatus(ResourceType type, ResourceStatus status);

	List<Resource> findByProgramIdAndTypeAndStatus(Long programId, ResourceType type, ResourceStatus status);
	
	// Total number of resources for a program
	@Query("""
			    SELECT COUNT(r)
			    FROM Resource r
			    WHERE r.programId = :programId
			""")
	Long countResources(Long programId);
	
	// Resource count grouped by ResourceType (FUNDS, LAB, EQUIPMENT)
	@Query("""
			    SELECT r.type AS type, COUNT(r) AS count
			    FROM Resource r
			    WHERE r.programId = :programId
			    GROUP BY r.type
			""")
	List<ResourceTypeCountProjection> countByType(Long programId);
	

	// Resource count grouped by ResourceStatus
	@Query("""
			    SELECT r.status AS status, COUNT(r) AS count
			    FROM Resource r
			    WHERE r.programId = :programId
			    GROUP BY r.status
			""")
	List<ResourceStatusCountProjection> countByStatus(Long programId);


	// FINANCIAL RESOURCES (FUNDS)
	// Fund amount grouped by status
	@Query("""
			    SELECT r.status AS status,
			           COALESCE(SUM(r.quantity), 0) AS totalAmount
			    FROM Resource r
			    WHERE r.programId = :programId
			      AND r.type = com.healthgov.enums.ResourceType.FUNDS
			    GROUP BY r.status
			""")
	List<FundAmountByStatusProjection> fundAmountByStatus(Long programId);

	// PHYSICAL RESOURCES (LAB & EQUIPMENT)
	// Physical resource quantity grouped by ResourceType (LAB / EQUIPMENT) and ResourceStatus
	@Query("""
			    SELECT r.type AS type,
			           r.status AS status,
			           COALESCE(SUM(r.quantity), 0) AS totalQuantity
			    FROM Resource r
			    WHERE r.programId = :programId
			      AND r.type IN (
			          com.healthgov.enums.ResourceType.LAB,
			          com.healthgov.enums.ResourceType.EQUIPMENT
			      )
			    GROUP BY r.type, r.status
			""")
	List<PhysicalQuantityProjection> physicalQuantitySummary(Long programId);


}