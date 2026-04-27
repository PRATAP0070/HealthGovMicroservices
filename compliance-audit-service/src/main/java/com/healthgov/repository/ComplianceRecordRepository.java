package com.healthgov.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.enums.ComplianceType;
import com.healthgov.models.ComplianceRecord;

@Repository
@Transactional
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

	Optional<ComplianceRecord> findOneByEntityIdAndType(Long entityId, ComplianceType type);

	boolean existsByEntityIdAndType(Long entityId, ComplianceType type);

	@Query("SELECT cr.type, COUNT(cr) FROM ComplianceRecord cr GROUP BY cr.type")
	List<Object[]> countByType();

	@Query("SELECT cr.result, COUNT(cr) FROM ComplianceRecord cr GROUP BY cr.result")
	List<Object[]> countByResult();

}