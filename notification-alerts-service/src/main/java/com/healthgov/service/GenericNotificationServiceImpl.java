package com.healthgov.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.healthgov.client.UserClient;
import com.healthgov.dto.ResearchProjectNotificationRequest;
import com.healthgov.dto.UserDTO;
import com.healthgov.enums.NotificationCategory;
import com.healthgov.enums.NotificationStatus;
import com.healthgov.model.Notification;
import com.healthgov.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenericNotificationServiceImpl implements GenericNotificationService {

	private final EmailService emailService;
	private final UserClient userClient;
	private final NotificationRepository notificationRepository;

	/**
	 * ===================================================== ENTRY POINT FOR ALL
	 * RESEARCH PROJECT NOTIFICATIONS
	 * =====================================================
	 */
	@Override
	public void notifyResearchProjectEvent(ResearchProjectNotificationRequest request) {

		log.info("Received Research Notification | eventType={} | projectId={}", request.getEventType(),
				request.getProjectId());

		String subject;
		String body;
		String recipientEmail;

		switch (request.getEventType()) {

		case "PROJECT_CREATED":
		case "PROJECT_UPDATED":
			subject = "Research Project Awaiting Review";
			body = buildManagerEmailBody(request);
			recipientEmail = resolveManagerEmail();
			break;

		case "PROJECT_APPROVED":
			subject = "Your Research Project Has Been Approved";
			body = buildApprovedEmailBody(request);
			recipientEmail = resolveResearcherEmail(request.getResearcherId());
			break;

		case "PROJECT_REJECTED":
			subject = "Your Research Project Has Been Rejected";
			body = buildRejectedEmailBody(request);
			recipientEmail = resolveResearcherEmail(request.getResearcherId());
			break;

		default:
			log.warn("Unknown eventType received: {}", request.getEventType());
			return;
		}

		/*
		 * ===================================================== 🔍 CRITICAL
		 * VERIFICATION LOG =====================================================
		 */
		log.info("Attempting to send notification email | eventType={} | to={}", request.getEventType(),
				recipientEmail);

		/*
		 * ===================================================== ✅ EMAIL MUST BE SENT
		 * FIRST (NON‑BLOCKING) =====================================================
		 */
		sendEmailSafely(recipientEmail, subject, body);

		/*
		 * ===================================================== ✅ DB SAVE IS SECONDARY
		 * (MUST NOT BLOCK EMAIL) =====================================================
		 */
		saveNotification(request.getProjectId(), body);
	}

	/*
	 * ===================================================== EMAIL BODY BUILDERS
	 * =====================================================
	 */

	private String buildManagerEmailBody(ResearchProjectNotificationRequest req) {
		return """
				A research project requires your review.

				Project ID   : %d
				Project Title: %s
				Status       : %s

				Please log in to the system to review the project.

				Regards,
				HealthGov Team
				""".formatted(req.getProjectId(), req.getProjectTitle(), req.getStatus());
	}

	private String buildApprovedEmailBody(ResearchProjectNotificationRequest req) {
		return """
				Congratulations!

				Your research project has been APPROVED.

				Project Title   : %s
				Approved Amount : %,.2f

				Regards,
				HealthGov Team
				""".formatted(req.getProjectTitle(), req.getApprovedAmount());
	}

	private String buildRejectedEmailBody(ResearchProjectNotificationRequest req) {
		return """
				Your research project has been REJECTED.

				Project Title : %s
				Reason        : %s

				Please review the feedback and resubmit if applicable.

				Regards,
				HealthGov Team
				""".formatted(req.getProjectTitle(), req.getRejectionReason());
	}

	/*
	 * ===================================================== EMAIL ADDRESS
	 * RESOLUTION =====================================================
	 */

	/**
	 * ⚠ IMPORTANT: This MUST be a REAL inbox you can open. Gmail suppresses
	 * self‑to‑self SMTP delivery.
	 */
	private String resolveManagerEmail() {
		return "your.real.manager.email@gmail.com"; // 🔴 MUST BE REAL
	}

	private String resolveResearcherEmail(Long researcherId) {
		UserDTO researcher = userClient.getUserById(researcherId);
		return researcher.getEmail();
	}

	/*
	 * ===================================================== DATABASE SAVE (SAFE /
	 * NON‑BLOCKING) =====================================================
	 */

	private void saveNotification(Long entityId, String message) {
		try {
			Notification notification = new Notification();
			notification.setEntityId(entityId);
			notification.setMessage(message);
			notification.setCategory(NotificationCategory.PROJECT); // ✅ REQUIRED
			notification.setStatus(NotificationStatus.SENT);
			notification.setCreatedDate(LocalDateTime.now());
			notification.setUserId(0L); // system / manager placeholder

			notificationRepository.save(notification);
		} catch (Exception ex) {
			log.error("Notification DB save failed (email already attempted). entityId={}", entityId, ex);
		}
	}

	/*
	 * ===================================================== EMAIL SEND (SAFE)
	 * =====================================================
	 */

	private void sendEmailSafely(String email, String subject, String body) {
		try {
			emailService.sendSimpleEmail(email, subject, body);
			log.info("Email send invoked successfully | to={}", email);
		} catch (Exception e) {
			log.error("Email sending failed | to={}", email, e);
		}
	}
}