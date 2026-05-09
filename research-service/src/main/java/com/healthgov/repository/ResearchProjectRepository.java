package com.healthgov.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.healthgov.enums.ProjectStatus;
import com.healthgov.model.ResearchProject;

public interface ResearchProjectRepository extends JpaRepository<ResearchProject, Long> {

    // ✅ SORTED: latest first
    List<ResearchProject> findAllByOrderByProjectIdDesc();

    // ✅ SORTED: by status + latest first
    List<ResearchProject> findByStatusOrderByProjectIdDesc(ProjectStatus status);

    boolean existsByProjectId(Long projectId);

}