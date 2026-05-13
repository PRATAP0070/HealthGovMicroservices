package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.healthgov.model.Citizen;

@Repository
public interface CitizenRepository extends JpaRepository<Citizen, Long> {
    
    Citizen findByUserId(Long userId);
    
}