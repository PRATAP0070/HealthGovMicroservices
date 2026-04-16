package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.model.Grants;

public interface GrantRepository extends JpaRepository<Grants, Long> {

	long countByProject_ProjectId(Long projectId);

	void deleteByProject_ProjectId(Long projectId);
}