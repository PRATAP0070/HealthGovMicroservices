package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.model.Infrastructure;
import com.healthgov.repository.projection.StatusCountProjection;
import com.healthgov.repository.projection.TypeCountProjection;
import com.healthgov.repository.projection.TypeStatusCapacityProjection;

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
			    SELECT i.status AS status, COUNT(i) AS count
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.status
			""")
	List<StatusCountProjection> countByStatus(Long programId);

	// Count by type
	@Query("""
			    SELECT i.type AS type, COUNT(i) AS count
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.type
			""")
	List<TypeCountProjection> countByType(Long programId);

	// Type → Status → Count + Capacity
	@Query("""
			    SELECT
			        i.type AS type,
			        i.status AS status,
			        COUNT(i) AS count,
			        COALESCE(SUM(i.capacity), 0) AS totalCapacity
			    FROM Infrastructure i
			    WHERE i.programId = :programId
			    GROUP BY i.type, i.status
			""")
	List<TypeStatusCapacityProjection> aggregateByTypeAndStatus(Long programId);

}