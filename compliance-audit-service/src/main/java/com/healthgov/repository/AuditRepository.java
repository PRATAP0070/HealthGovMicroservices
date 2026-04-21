package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.enums.AuditStatus;
import com.healthgov.models.Audit;

@Repository
@Transactional
public interface AuditRepository extends JpaRepository<Audit, Long> {

	List<Audit> findByStatus(AuditStatus status);

	List<Audit> findByOfficerId(Long officerId);

	boolean existsByOfficerIdAndScopeIgnoreCase(Long userId, String scope);

}
