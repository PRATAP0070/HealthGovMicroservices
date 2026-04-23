package com.healthgov.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResearchProjectNotificationRequest {

    private String toRole;
    private String eventType;

    private Long projectId;
    private String projectTitle;

    private Long researcherId;   // ✅ ADD THIS

    private String status;
    private Double approvedAmount;
    private String rejectionReason;
    private String message;
}