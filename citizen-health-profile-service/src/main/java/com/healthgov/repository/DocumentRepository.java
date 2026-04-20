package com.healthgov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.healthgov.model.CitizenDocument;

@Repository
public interface DocumentRepository extends JpaRepository<CitizenDocument, Long> {
    
}