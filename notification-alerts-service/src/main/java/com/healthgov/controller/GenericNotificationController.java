package com.healthgov.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.healthgov.dto.ResearchProjectNotificationRequest;
import com.healthgov.service.GenericNotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class GenericNotificationController {

    private final GenericNotificationService genericNotificationService;

    @PostMapping("/research-project")
    public ResponseEntity<Void> handleResearchProjectNotification(
            @RequestBody ResearchProjectNotificationRequest request) {

        log.info(
            "Received Research Project Notification | eventType={} | projectId={}",
            request.getEventType(),
            request.getProjectId()
        );

        genericNotificationService.notifyResearchProjectEvent(request);

        return ResponseEntity.ok().build();
    }
}
