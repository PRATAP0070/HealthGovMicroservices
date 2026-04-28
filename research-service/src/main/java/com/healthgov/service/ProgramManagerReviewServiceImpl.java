package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthgov.client.NotificationClient;
import com.healthgov.client.UserClient;
import com.healthgov.dto.ResearchProjectResponse;
import com.healthgov.dto.UniversalNotificationRequest;
import com.healthgov.dto.UserReqDTO;
import com.healthgov.enums.GrantStatus;
import com.healthgov.enums.NotificationCategory;
import com.healthgov.enums.ProjectStatus;
import com.healthgov.events.GrantCreatedEvent;
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
	private final ApplicationEventPublisher eventPublisher;
	private final UserClient userClient;
	private final NotificationClient notificationClient;

	// Get project by id
	@Override
	@Transactional(readOnly = true)
	public ResearchProjectResponse getProject(Long projectId) {

		log.info("Fetching project for PM review: projectId={}", projectId);

		ResearchProject p = projectRepo.findById(projectId).orElseThrow(() -> {
			log.error("Project NOT FOUND: projectId={}", projectId);
			return new MedicalResearchException(HttpStatus.NOT_FOUND, "Project not found: " + projectId);
		});

		log.info("Project fetched successfully: projectId={}", projectId);
		return toResponse(p);
	}

	// List pending projects
	@Override
	@Transactional(readOnly = true)
	public List<ResearchProjectResponse> listPending() {

		log.info("Fetching ALL PENDING projects for PM dashboard.");

		List<ResearchProject> pending = projectRepo.findByStatus(ProjectStatus.PENDING);

		log.info("Total pending projects found: {}", pending.size());
		return toResponseList(pending);
	}

	// List projects by status
	@Override
	@Transactional(readOnly = true)
	public List<ResearchProjectResponse> listByStatus(String status) {

		log.info("Fetching projects with status={}", status);

		ProjectStatus s;
		try {
			s = ProjectStatus.valueOf(status.toUpperCase());
		} catch (Exception ex) {
			log.error("Invalid status received: {}", status);
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "Allowed: PENDING, APPROVED, REJECTED");
		}

		List<ResearchProject> list = projectRepo.findByStatus(s);
		log.info("Projects found with status {}: {}", status, list.size());

		return toResponseList(list);
	}

	// PM Decision
	@Override
	public ResearchProjectResponse decide(Long projectId, String decision, String reason, Double amount) {

		log.info("PM decision received: projectId={}, decision={}", projectId, decision);

		if (decision == null || decision.isBlank()) {
			log.error("PM decision missing for projectId={}", projectId);
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "decision is required");
		}

		ProjectStatus d;
		try {
			d = ProjectStatus.valueOf(decision.toUpperCase());
		} catch (Exception e) {
			log.error("Invalid decision={} for projectId={}", decision, projectId);
			throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "Allowed: APPROVED, REJECTED");
		}

		ResearchProject p = projectRepo.findById(projectId).orElseThrow(() -> {
			log.error("Project not found during PM decision: projectId={}", projectId);
			return new MedicalResearchException(HttpStatus.NOT_FOUND, "Project not found: " + projectId);
		});

		log.info("Project found. Applying decision={} for projectId={}", decision, projectId);

		GrantApplication ga = grantApplicationRepo.findTopByProject_ProjectIdOrderByApplicationIdDesc(projectId);

		// REJECTED
		if (d == ProjectStatus.REJECTED) {

			log.warn("Rejecting project: projectId={}", projectId);

			if (reason == null || reason.isBlank()) {
				log.error("Rejection reason missing for projectId={}", projectId);
				throw new MedicalResearchException(HttpStatus.BAD_REQUEST, "reason is required when REJECTED");
			}

			p.setReason(reason);
			ga.setStatus(GrantStatus.REJECTED);
			grantApplicationRepo.save(ga);

			log.info("Project rejected: projectId={}, reason={}", projectId, reason);
		}

		// APPROVED
		else if (d == ProjectStatus.APPROVED) {

			log.info("Approving project: projectId={}", projectId);

			if (amount == null || amount <= 0) {
				log.error("Missing/invalid amount for APPROVED projectId={}", projectId);
				throw new MedicalResearchException(HttpStatus.BAD_REQUEST,
						"amount must be provided when APPROVED & amount > 0");
			}

			ga.setStatus(GrantStatus.APPROVED);
			grantApplicationRepo.save(ga);

			log.info("GrantApplication updated to APPROVED for projectId={}", projectId);

			// Create grant if not exists
			if (grantRepo.countByProject_ProjectId(projectId) == 0) {

				log.info("No existing grant found. Creating new grant for projectId={}, amount={}", projectId, amount);

				Grants g = new Grants();
				g.setProject(p);
				g.setResearcherId(p.getResearcherId()); // MICROservice change
				g.setDate(LocalDateTime.now());
				g.setAmount(amount);
				g.setStatus(GrantStatus.APPROVED);

				grantRepo.save(g);

				eventPublisher.publishEvent(new GrantCreatedEvent(g.getGrantId(), g.getProject().getProjectId()));

				log.info("Grant created successfully: projectId={}, amount={}", projectId, amount);
			}
		}

		p.setStatus(d);
		ResearchProject saved = projectRepo.save(p);

		// ✅ ✅ ✅ ADD NOTIFICATION CODE RIGHT HERE
		try {
			UserReqDTO researcher = userClient.getUserById(p.getResearcherId());

			UniversalNotificationRequest notification = new UniversalNotificationRequest();

			notification.setUserId(researcher.getUserId());
			notification.setEmail(researcher.getEmail());
			notification.setCategory(NotificationCategory.PROJECT);

			if (d == ProjectStatus.APPROVED) {
				notification.setMessage("✅ Your research project has been APPROVED.\n\n" + "Project ID: " + projectId
						+ "\n" + "Project Title: " + saved.getTitle() + "\n" + "Approved Amount: " + amount);
			} else {
				notification.setMessage("❌ Your research project has been REJECTED.\n\n" + "Project ID: " + projectId
						+ "\n" + "Project Title: " + saved.getTitle() + "\n" + "Reason: " + reason);
			}

			notification.setEntityId(projectId);

			notificationClient.sendUniversalNotification(notification);

		} catch (Exception ex) {
			log.warn("Researcher notification failed, PM decision succeeded for projectId={}", projectId, ex);
		}

		log.info("Project decision applied successfully: projectId={}, finalStatus={}", projectId, d);

		return toResponse(saved);
	}

	// DTO Mapping

	private List<ResearchProjectResponse> toResponseList(List<ResearchProject> list) {
		List<ResearchProjectResponse> out = new ArrayList<>();
		for (ResearchProject p : list)
			out.add(toResponse(p));
		return out;
	}

	private ResearchProjectResponse toResponse(ResearchProject p) {

		// ✅ Fetch researcher details
		UserReqDTO researcher = userClient.getUserById(p.getResearcherId());

		ResearchProjectResponse r = new ResearchProjectResponse();
		r.setProjectId(p.getProjectId());
		r.setTitle(p.getTitle());
		r.setDescription(p.getDescription());
		r.setStartDate(p.getStartDate());
		r.setEndDate(p.getEndDate());
		r.setStatus(p.getStatus().name());
		r.setReason(p.getReason());

		r.setResearcherId(p.getResearcherId());
		r.setResearcherName(researcher.getName()); // ✅ FIX

		return r;
	}

}