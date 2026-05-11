package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.model.Infrastructure;

public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {

	List<Infrastructure> findByProgramId(Long programId);

//	List<Infrastructure> findByTypeAndLocationAndStatus(InfrastructureType type, String location,
//			InfrastructureStatus status);

	@Query("""
			    SELECT i FROM Infrastructure i
			    WHERE (:type IS NULL OR i.type = :type)
			      AND (:location IS NULL OR i.location = :location)
			      AND (:status IS NULL OR i.status = :status)
			""")
	List<Infrastructure> searchInfra(@Param("type") InfrastructureType type, @Param("location") String location,
			@Param("status") InfrastructureStatus status);

	@Query("""
			    SELECT i FROM Infrastructure i
			    WHERE :programId = i.programId
				   AND (:type IS NULL OR i.type = :type)
			      AND (:location IS NULL OR i.location = :location)
			      AND (:status IS NULL OR i.status = :status)
			""")
	List<Infrastructure> searchInfraByProgram(@Param("programId")Long programId, @Param("type") InfrastructureType type,
			@Param("location") String location, @Param("status") InfrastructureStatus status);

	@Query("SELECT COUNT(i) FROM Infrastructure i WHERE i.type = :type")
	Long countByType(InfrastructureType type);

	@Query("SELECT COUNT(i) FROM Infrastructure i WHERE i.type = :type AND i.status = :status")
	Long countByTypeAndStatus(InfrastructureType type, InfrastructureStatus status);

	@Query("SELECT COUNT(i) FROM Infrastructure i")
	Long countTotal();

}