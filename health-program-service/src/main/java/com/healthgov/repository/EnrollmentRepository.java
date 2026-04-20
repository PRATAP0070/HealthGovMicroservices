package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.healthgov.model.Enrollment;

@Repository // Optional but good practice for clarity
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    // Note: Replace 'Long' with whatever data type your primary key is in the Enrollment model.
}