package com.healthgov.service;

import com.healthgov.client.UserClient;
import com.healthgov.dto.CreateNotificationRequest;
import com.healthgov.dto.NotificationDTO;
import com.healthgov.dto.UserDTO;
import com.healthgov.enums.NotificationCategory;
import com.healthgov.model.Notification;
import com.healthgov.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final UserClient userClient;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   EmailService emailService,
                                   UserClient userClient) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
        this.userClient = userClient;
    }

    @Override
    public NotificationDTO sendNotification(CreateNotificationRequest request) {

        // ✅ Fetch user via Feign
        UserDTO user = userClient.getUserById(request.getUserId());

        Notification notification = new Notification();
        notification.setUserId(user.getUserId());
        notification.setUserEmail(user.getEmail());
        notification.setEntityId(request.getEntityId());
        notification.setMessage(request.getMessage());
        notification.setCategory(
                NotificationCategory.valueOf(request.getCategory().toUpperCase())
        );
        notification.setStatus("UNREAD");
        notification.setCreatedDate(LocalDateTime.now());

        // ✅ SAVE TO DB (YOU WERE MISSING THIS)
        Notification saved = notificationRepository.save(notification);

        // ✅ Send email (safe)
        emailService.sendSimpleEmail(
                user.getEmail(),
                "New Notification",
                request.getMessage()
        );

        // ✅ MANUAL MAPPING (NO map() METHOD)
        NotificationDTO dto = new NotificationDTO();
        dto.setNotificationId(saved.getNotificationId());
        dto.setUserId(saved.getUserId());
        dto.setEntityId(saved.getEntityId());
        dto.setMessage(saved.getMessage());
        dto.setCategory(saved.getCategory());
        dto.setStatus(saved.getStatus());
        dto.setCreatedDate(saved.getCreatedDate());

        return dto;
    }

    @Override
    public List<NotificationDTO> getUserNotifications(Long userId) {

        return notificationRepository.findByUserId(userId)
                .stream()
                .map(n -> {
                    NotificationDTO dto = new NotificationDTO();
                    dto.setNotificationId(n.getNotificationId());
                    dto.setUserId(n.getUserId());
                    dto.setEntityId(n.getEntityId());
                    dto.setMessage(n.getMessage());
                    dto.setCategory(n.getCategory());
                    dto.setStatus(n.getStatus());
                    dto.setCreatedDate(n.getCreatedDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setStatus("READ");
        notificationRepository.save(notification);
    }
}