package com.healthgov.repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.model.AduitLog;


public interface AuditLogRepo extends JpaRepository<AduitLog, Long> {

	List<AduitLog> findByUserId(Long userId);
}
