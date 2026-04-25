package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
