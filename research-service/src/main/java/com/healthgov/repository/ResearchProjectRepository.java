package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.ProjectStatus;
import com.healthgov.model.ResearchProject;

public interface ResearchProjectRepository extends JpaRepository<ResearchProject, Long> {

	List<ResearchProject> findByStatus(ProjectStatus status);

}