package com.healthgov.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.client.UserClient;
import com.healthgov.dto.ResearchProjectCreateRequest;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.dto.ResearchProjectUpdateRequest;
import com.healthgov.enums.GrantStatus;
import com.healthgov.enums.ProjectStatus;
import com.healthgov.exceptions.MedicalResearchException;
import com.healthgov.model.GrantApplication;
import com.healthgov.model.ResearchProject;
import com.healthgov.repository.GrantApplicationRepository;
import com.healthgov.repository.GrantRepository;
import com.healthgov.repository.ResearchProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResearchProjectServiceImpl implements ResearchProjectService {

    private final ResearchProjectRepository projectRepo;
    private final GrantApplicationRepository grantApplicationRepo;
    private final GrantRepository grantRepo;
    private final UserClient userClient; // ✅ Feign client

    @Override
    public ResearchProjectResponse create(ResearchProjectCreateRequest req) {

        log.info("Creating research project for researcherId={}", req.getResearcherId());

        if (req.getStartDate().isBefore(LocalDate.now())) {
            throw new MedicalResearchException(
                    HttpStatus.BAD_REQUEST, "startDate cannot be in the past");
        }

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new MedicalResearchException(
                    HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
        }

        // ✅ Validate researcher via User Service
        userClient.getUserById(req.getResearcherId());

        ResearchProject project = new ResearchProject();
        project.setTitle(req.getTitle());
        project.setDescription(req.getDescription());
        project.setResearcherId(req.getResearcherId());
        project.setStartDate(req.getStartDate());
        project.setEndDate(req.getEndDate());
        project.setStatus(ProjectStatus.PENDING);
        project.setReason(null);

        ResearchProject saved = projectRepo.save(project);

        GrantApplication ga = new GrantApplication();
        ga.setProject(saved);
        ga.setResearcherId(req.getResearcherId());
        ga.setStatus(GrantStatus.PENDING);
        ga.setSubmittedDate(LocalDate.now());
        grantApplicationRepo.save(ga);

        return toResponse(saved);
    }

    @Override
    public ResearchProjectResponse update(ResearchProjectUpdateRequest req) {

        ResearchProject project = projectRepo.findById(req.getProjectId())
                .orElseThrow(() -> new MedicalResearchException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        if (project.getStatus() == ProjectStatus.APPROVED) {
            throw new MedicalResearchException(
                    HttpStatus.BAD_REQUEST, "Approved projects cannot be updated");
        }

        project.setTitle(req.getTitle());
        project.setDescription(req.getDescription());
        project.setStartDate(req.getStartDate());
        project.setEndDate(req.getEndDate());
        project.setStatus(ProjectStatus.PENDING);
        project.setReason(null);

        ResearchProject saved = projectRepo.save(project);

        GrantApplication ga = new GrantApplication();
        ga.setProject(saved);
        ga.setResearcherId(saved.getResearcherId());
        ga.setStatus(GrantStatus.PENDING);
        ga.setSubmittedDate(LocalDate.now());
        grantApplicationRepo.save(ga);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchProjectResponse> list(String status) {

        List<ResearchProject> projects;

        if (status != null && !status.isBlank()) {
            try {
                projects = projectRepo.findByStatus(
                        ProjectStatus.valueOf(status.toUpperCase()));
            } catch (Exception e) {
                throw new MedicalResearchException(
                        HttpStatus.BAD_REQUEST, "Invalid project status");
            }
        } else {
            projects = projectRepo.findAll();
        }

        return toResponseList(projects);
    }

    @Override
    @Transactional(readOnly = true)
    public ResearchProjectResponse get(Long id) {

        ResearchProject project = projectRepo.findById(id)
                .orElseThrow(() -> new MedicalResearchException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        return toResponse(project);
    }

    @Override
    public void delete(Long id) {

        ResearchProject project = projectRepo.findById(id)
                .orElseThrow(() -> new MedicalResearchException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        if (project.getStatus() != ProjectStatus.PENDING) {
            throw new MedicalResearchException(
                    HttpStatus.CONFLICT,
                    "Only PENDING projects can be deleted");
        }

        grantRepo.deleteByProject_ProjectId(id);
        grantApplicationRepo.deleteByProject_ProjectId(id);
        projectRepo.delete(project);
    }

    /* ---------- DTO Mapping ---------- */

    private ResearchProjectResponse toResponse(ResearchProject p) {
        ResearchProjectResponse r = new ResearchProjectResponse();
        r.setProjectId(p.getProjectId());
        r.setTitle(p.getTitle());
        r.setDescription(p.getDescription());
        r.setStartDate(p.getStartDate());
        r.setEndDate(p.getEndDate());
        r.setStatus(p.getStatus().name());
        r.setReason(p.getReason());
        r.setResearcherId(p.getResearcherId());
        return r;
    }

    private List<ResearchProjectResponse> toResponseList(List<ResearchProject> list) {
        List<ResearchProjectResponse> out = new ArrayList<>();
        for (ResearchProject p : list) {
            out.add(toResponse(p));
        }
        return out;
    }
}