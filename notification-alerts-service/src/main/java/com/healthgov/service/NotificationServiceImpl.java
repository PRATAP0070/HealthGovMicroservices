package com.healthgov.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.healthgov.client.UserClient;
import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.UniversalNotificationRequest;
import com.healthgov.dto.UserDTO;
import com.healthgov.enums.NotificationCategory;
import com.healthgov.enums.NotificationStatus;
import com.healthgov.exception.BadRequestException;
import com.healthgov.exception.NotFoundException;
import com.healthgov.model.Notification;
import com.healthgov.repository.NotificationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final EmailService emailService;
	private final UserClient userClient;

	public NotificationServiceImpl(NotificationRepository notificationRepository, EmailService emailService,
			UserClient userClient) {
		this.notificationRepository = notificationRepository;
		this.emailService = emailService;
		this.userClient = userClient;
	}

	// ================= SEND NOTIFICATION =================
	@Override
	public NotificationDTO sendNotification(CreateNotificationRequest request) {

		// ===== FEIGN CALL (MATCHES FRIEND) =====
		UserDTO user = userClient.getUserById(request.getUserId());

		log.info("User Client Data : {}", user);
		String email = user.getEmail();

		// ===== MESSAGE LOGIC (CATEGORY-BASED, LIKE FRIEND) =====
		String message;

		switch (request.getCategory()) {

		case PROJECT:
			message = "Project-related notification.";
			break;

		case COMPLIANCE:
			message = "Compliance-related notification.";
			break;

		case GRANT:
			message = "Grant-related update.";
			break;

		case PROGRAM:
			message = "Program update notification.";
			break;

		case GENERAL:
			if (request.getMessage() == null || request.getMessage().isEmpty()) {
				throw new BadRequestException("Message is required for GENERAL category");
			}
			message = request.getMessage();
			break;

		default:
			message = "Notification";
		}

		// ===== SAVE NOTIFICATION =====
		Notification notification = new Notification();
		notification.setUserId(user.getUserId()); // ✅ MATCH USER-SERVICE RESPONSE
		notification.setEntityId(request.getEntityId());
		notification.setCategory(request.getCategory()); // ✅ ENUM DIRECTLY
		notification.setMessage(message);
		notification.setStatus(NotificationStatus.SENT); // ✅ ENUM STATUS
		notification.setCreatedDate(LocalDateTime.now());

		Notification saved = notificationRepository.save(notification);

		// ===== EMAIL (USING YOUR EmailService) =====
		emailService.sendSimpleEmail(email, "HealthGov Notification", message + "\n\nRegards,\nHealthGov Team");

		// ===== MAP TO DTO =====
		return mapToDTO(saved);
	}

	// ================= GET ALL NOTIFICATIONS =================
	@Override
	public List<NotificationDTO> getAllNotifications() {

		return notificationRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// ================= GET BY USER =================
	@Override
	public List<NotificationDTO> getUserNotifications(Long userId) {

		return notificationRepository.findByUserId(userId).stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// ================= MARK AS READ =================
	@Override
	public void markAsRead(Long id) {

		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Notification not found"));

		notification.setStatus(NotificationStatus.READ);
		notificationRepository.save(notification);
	}
	// Universal Notification

	@Override
	public void sendUniversalNotification(UniversalNotificationRequest request) {

		if (request.getEmail() == null || request.getEmail().isBlank()) {
			throw new BadRequestException("Email is required");
		}

		if (request.getMessage() == null || request.getMessage().isBlank()) {
			throw new BadRequestException("Message is required");
		}

		// ✅ Save notification
		Notification notification = new Notification();
		notification.setUserId(request.getUserId());
		notification.setEntityId(request.getEntityId());
		notification.setCategory(request.getCategory() != null ? request.getCategory() : NotificationCategory.GENERAL);
		notification.setMessage(request.getMessage());
		notification.setStatus(NotificationStatus.SENT);
		notification.setCreatedDate(LocalDateTime.now());

		notificationRepository.save(notification);

		// ✅ Send email
		emailService.sendSimpleEmail(request.getEmail(), "HealthGov Notification", request.getMessage());

		log.info("Universal notification sent to {}", request.getEmail());
	}

	// ================= DELETE =================
	@Override
	public void deleteNotification(Long id) {

		if (!notificationRepository.existsById(id)) {
			throw new NotFoundException("Notification not found");
		}

		notificationRepository.deleteById(id);
	}

	// ================= COMMON ENTITY → DTO MAPPER =================
	private NotificationDTO mapToDTO(Notification n) {

		NotificationDTO dto = new NotificationDTO();
		dto.setNotificationId(n.getId());
		dto.setUserId(n.getUserId());
		dto.setEntityId(n.getEntityId());
		dto.setMessage(n.getMessage());
		dto.setCategory(n.getCategory());
		dto.setStatus(n.getStatus().name());
		dto.setCreatedDate(n.getCreatedDate());

		return dto;
	}
}