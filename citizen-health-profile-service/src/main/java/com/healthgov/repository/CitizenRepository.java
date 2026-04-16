package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.model.Citizen;

public interface CitizenRepository extends JpaRepository<Citizen, Long> {
  
}