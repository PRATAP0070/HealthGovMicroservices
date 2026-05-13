package com.healthgov.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.healthgov.model.HealthProfile;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
    
    // This tells JPA: "When you run the standard findAll, 
    // also bring the 'citizen' data immediately."
    @EntityGraph(attributePaths = {"citizen"})
    List<HealthProfile> findAll();

    Optional<HealthProfile> findByCitizen_CitizenId(Long citizenId);
    boolean existsByCitizen_CitizenId(Long citizenId);
}