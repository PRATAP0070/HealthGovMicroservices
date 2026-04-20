package com.healthgov.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.healthgov.model.HealthProfile;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
	
	Optional<HealthProfile> findByCitizen_CitizenId(Long citizenId);
}