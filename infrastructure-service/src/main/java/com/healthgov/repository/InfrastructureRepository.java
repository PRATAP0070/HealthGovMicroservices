package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.model.Infrastructure;

public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {

	List<Infrastructure> findByProgramId(Long programId);

	List<Infrastructure> findByTypeAndLocationAndStatus(InfrastructureType type, String location,
			InfrastructureStatus status);
	

	// Total capacity
	@Query("""
			    SELECT COALESCE(SUM(i.capacity), 0)
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			""")
	Long sumCapacityByProgramId(Long programId);

	// Count by status
	@Query("""
			    SELECT i.status, COUNT(i)
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.status
			""")
	List<Object[]> countByStatus(Long programId);
	
	// Count by type
	@Query("""
			    SELECT i.type, COUNT(i)
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.type
			""")
	List<Object[]> countByType(Long programId);

	// Type → Status → Count + Capacity
	@Query("""
			    SELECT i.type, i.status, COUNT(i), COALESCE(SUM(i.capacity), 0)
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.type, i.status
			""")
	List<Object[]> aggregateByTypeAndStatus(Long programId);


}