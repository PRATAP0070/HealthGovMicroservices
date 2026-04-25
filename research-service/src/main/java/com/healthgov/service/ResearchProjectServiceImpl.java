package com.healthgov.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.client.UserClient;
import com.healthgov.dto.ResearchProjectCreateRequest;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.dto.ResearchProjectUpdateRequest;
import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.GrantStatus;
import com.healthgov.enums.ProjectStatus;
import com.healthgov.enums.Role;
import com.healthgov.events.ProjectCreatedEvent;
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
	private final GrantRepository grantRepo;
	private final GrantApplicationRepository grantApplicationRepo;
	private final UserClient userClient;
	private final ApplicationEventPublisher eventPublisher;

	// Create project
	@Override
	public ResearchProjectResponse create(ResearchProjectCreateRequest req) {

		log.info("Creating new research project for researcherId={}", req.getResearcherId());

		LocalDate today = LocalDate.now();

		if (req.getStartDate().isBefore(today)) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "startDate cannot be in the past");
		}

		if (req.getEndDate().isBefore(req.getStartDate())) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
		}

		// Validate researcher existence + role
		UserReqDTO user;
		try {
			user = userClient.getUserById(req.getResearcherId());
		} catch (Exception e) {
			throw new MedicalResearchException(HttpStatus.NOT_FOUND, "Researcher not found: " + req.getResearcherId());
		}

		if (user.getRole() != Role.RESEARCHER) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST,
					"Only users with RESEARCHER role can create research projects");
		}

		ResearchProject p = new ResearchProject();
		p.setTitle(req.getTitle());
		p.setDescription(req.getDescription());
		p.setResearcherId(req.getResearcherId());
		p.setStartDate(req.getStartDate());
		p.setEndDate(req.getEndDate());
		p.setStatus(ProjectStatus.PENDING);
		p.setReason(null);

		ResearchProject saved = projectRepo.save(p);

		// Creating grant application
		GrantApplication ga = new GrantApplication();
		ga.setProject(saved);
		ga.setResearcherId(req.getResearcherId());
		ga.setSubmittedDate(LocalDate.now());
		ga.setStatus(GrantStatus.PENDING);
		grantApplicationRepo.save(ga);
		
		
		//Calling compliance Client  for Grant 
		eventPublisher.publishEvent(new ProjectCreatedEvent(saved.getProjectId(), saved.getTitle()));
		log.info("Compliance Create Event Triggred...");

		return toResponse(saved);
	}

	// Update project
	@Override
	public ResearchProjectResponse update(ResearchProjectUpdateRequest req) {

		log.info("Updating research project with projectId={}", req.getProjectId());

		ResearchProject p = projectRepo.findById(req.getProjectId()).orElseThrow(
				() -> new MedicalResearchException(HttpStatus.NOT_FOUND, "Project not found: " + req.getProjectId()));

		// Validate existing researcher + role
		UserReqDTO user;
		try {
			user = userClient.getUserById(p.getResearcherId());
		} catch (Exception e) {
			throw new MedicalResearchException(HttpStatus.NOT_FOUND, "Researcher not found: " + p.getResearcherId());
		}

		if (user.getRole() != Role.RESEARCHER) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "Project researcher must have RESEARCHER role");
		}

		LocalDate today = LocalDate.now();

		if (req.getStartDate().isBefore(today)) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "startDate cannot be in the past");
		}

		if (req.getEndDate().isBefore(req.getStartDate())) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "endDate cannot be before startDate");
		}

		if (p.getStatus() == ProjectStatus.APPROVED) {
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "Approved projects cannot be updated.");
		}

		p.setTitle(req.getTitle());
		p.setDescription(req.getDescription());
		p.setStartDate(req.getStartDate());
		p.setEndDate(req.getEndDate());
		p.setStatus(ProjectStatus.PENDING);
		p.setReason(null);

		ResearchProject saved = projectRepo.save(p);

		GrantApplication ga = new GrantApplication();
		ga.setProject(saved);
		ga.setResearcherId(saved.getResearcherId());
		ga.setSubmittedDate(LocalDate.now());
		ga.setStatus(GrantStatus.PENDING);
		grantApplicationRepo.save(ga);

		return toResponse(saved);
	}

	// List projects
	@Override
	@Transactional(readOnly = true)
	public List<ResearchProjectResponse> list(String status) {

		List<ResearchProject> projects;

		if (status != null && !status.isBlank()) {
			try {
				ProjectStatus s = ProjectStatus.valueOf(status.toUpperCase());
				projects = projectRepo.findByStatus(s);
			} catch (IllegalArgumentException e) {
				throw new MedicalResearchException(HttpStatus.BAD_REQUEST,
						"Invalid status. Allowed: PENDING, APPROVED, REJECTED");
			}
		} else {
			projects = projectRepo.findAll();
		}

		return toResponseList(projects);
	}

	// Get project by id
	@Override
	@Transactional(readOnly = true)
	public ResearchProjectResponse get(Long id) {

		ResearchProject p = projectRepo.findById(id)
				.orElseThrow(() -> new MedicalResearchException(HttpStatus.NOT_FOUND, "Project not found: " + id));

		return toResponse(p);
	}

	// Delete project

	@Override
	public void delete(Long id) {

		ResearchProject p = projectRepo.findById(id)
				.orElseThrow(() -> new MedicalResearchException(HttpStatus.NOT_FOUND, "Project not found: " + id));

		if (p.getStatus() == ProjectStatus.PENDING) {
			grantRepo.deleteByProject_ProjectId(id);
			grantApplicationRepo.deleteByProject_ProjectId(id);
			projectRepo.delete(p);
			return;
		}

		throw new MedicalResearchException(HttpStatus.CONFLICT, "Cannot delete project with APPROVED/REJECTED status.");
	}

	@Override
	@Transactional(readOnly = true)
	public boolean projectExists(Long projectId) {
		return projectRepo.existsByProjectId(projectId);
	}

	// DTO mapping
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