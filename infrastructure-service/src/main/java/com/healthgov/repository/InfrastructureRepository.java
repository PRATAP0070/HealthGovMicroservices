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

	@Query("SELECT COUNT(i) FROM Infrastructure i WHERE i.type = :type")
	Long countByType(InfrastructureType type);

	@Query("SELECT COUNT(i) FROM Infrastructure i WHERE i.type = :type AND i.status = :status")
	Long countByTypeAndStatus(InfrastructureType type, InfrastructureStatus status);

	@Query("SELECT COUNT(i) FROM Infrastructure i")
	Long countTotal();

}