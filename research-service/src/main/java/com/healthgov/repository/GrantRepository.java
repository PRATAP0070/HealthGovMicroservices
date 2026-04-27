package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.healthgov.enums.GrantStatus;
import com.healthgov.model.Grants;

public interface GrantRepository extends JpaRepository<Grants, Long> {

	long countByProject_ProjectId(Long projectId);

	void deleteByProject_ProjectId(Long projectId);

	boolean existsByGrantId(Long grantId);

}