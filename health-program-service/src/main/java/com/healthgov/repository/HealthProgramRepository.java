package com.healthgov.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.healthgov.model.HealthProgram;
@Repository
public interface HealthProgramRepository extends JpaRepository<HealthProgram, Long> { }