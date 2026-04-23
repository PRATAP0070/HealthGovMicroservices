package com.healthgov.service;

import com.healthgov.dto.ResearchProjectNotificationRequest;

public interface GenericNotificationService {
    void notifyResearchProjectEvent(
        ResearchProjectNotificationRequest request
    );
}