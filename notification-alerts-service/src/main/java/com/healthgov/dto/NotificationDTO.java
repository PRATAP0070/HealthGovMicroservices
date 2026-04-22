package com.healthgov.dto;

import com.healthgov.enums.NotificationCategory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDTO {

    private Long notificationId;
    private Long userId;
    private Long entityId;
    private String message;
    private NotificationCategory category;
    private String status;
    private LocalDateTime createdDate;
}
