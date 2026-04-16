package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.enums.GrantStatus;
import com.healthgov.enums.ProjectStatus;
import com.healthgov.exceptions.MedicalResearchException;
import com.healthgov.model.GrantApplication;
import com.healthgov.model.Grants;
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
public class ProgramManagerReviewServiceImpl implements ProgramManagerReviewService {

    private final ResearchProjectRepository projectRepo;
    private final GrantRepository grantRepo;
    private final GrantApplicationRepository grantApplicationRepo;

    @Override
    @Transactional(readOnly = true)
    public ResearchProjectResponse getProject(Long projectId) {

        ResearchProject project = projectRepo.findById(projectId)
                .orElseThrow(() -> new MedicalResearchException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        return toResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchProjectResponse> listPending() {
        return toResponseList(
                projectRepo.findByStatus(ProjectStatus.PENDING));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResearchProjectResponse> listByStatus(String status) {

        ProjectStatus ps;
        try {
            ps = ProjectStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new MedicalResearchException(
                    HttpStatus.BAD_REQUEST,
                    "Allowed values: PENDING, APPROVED, REJECTED");
        }

        return toResponseList(projectRepo.findByStatus(ps));
    }

    @Override
    public ResearchProjectResponse decide(
            Long projectId, String decision, String reason, Double amount) {

        ResearchProject project = projectRepo.findById(projectId)
                .orElseThrow(() -> new MedicalResearchException(
                        HttpStatus.NOT_FOUND, "Project not found"));

        ProjectStatus ps;
        try {
            ps = ProjectStatus.valueOf(decision.toUpperCase());
        } catch (Exception e) {
            throw new MedicalResearchException(
                    HttpStatus.BAD_REQUEST, "Invalid decision");
        }

        GrantApplication ga =
                grantApplicationRepo
                        .findTopByProject_ProjectIdOrderByApplicationIdDesc(projectId);

        if (ps == ProjectStatus.REJECTED) {

            if (reason == null || reason.isBlank()) {
                throw new MedicalResearchException(
                        HttpStatus.BAD_REQUEST,
                        "Reason is mandatory when rejecting");
            }

            project.setReason(reason);
            ga.setStatus(GrantStatus.REJECTED);
        }

        if (ps == ProjectStatus.APPROVED) {

            if (amount == null || amount < 0) {
                throw new MedicalResearchException(
                        HttpStatus.BAD_REQUEST,
                        "Valid amount required for approval");
            }

            ga.setStatus(GrantStatus.APPROVED);

            if (grantRepo.countByProject_ProjectId(projectId) == 0) {

                Grants grant = new Grants();
                grant.setProject(project);
                grant.setResearcherId(project.getResearcherId());
                grant.setAmount(amount);
                grant.setDate(LocalDateTime.now());
                grant.setStatus(GrantStatus.APPROVED);

                grantRepo.save(grant);
            }
        }

        project.setStatus(ps);
        projectRepo.save(project);
        grantApplicationRepo.save(ga);

        return toResponse(project);
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
