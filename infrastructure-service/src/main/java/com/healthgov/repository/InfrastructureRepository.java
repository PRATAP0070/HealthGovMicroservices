package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.InfrastructureStatus;
import com.healthgov.enums.InfrastructureType;
import com.healthgov.model.Infrastructure;

public interface InfrastructureRepository extends JpaRepository<Infrastructure, Long> {

	List<Infrastructure> findByProgramId(Long programId);

	List<Infrastructure> findByTypeAndLocationAndStatus(InfrastructureType type, String location,
			InfrastructureStatus status);
}