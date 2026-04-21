package com.healthgov.Listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.healthgov.client.ComplianceClient;
import com.healthgov.dto.ComplianceCreateRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.events.ProjectCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectComplianceListener {

    private final ComplianceClient complianceClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ProjectCreatedEvent event) {

    	log.info("Running Project Complinace Listener");
    	
        ComplianceCreateRequest request = new ComplianceCreateRequest();
        request.setType(ComplianceType.PROJECT);
        request.setEntityId(event.getProjectId());
        request.setResult(ComplianceResult.UNDER_REVIEW.name());
        request.setNotes(
            "Compliance created for Project: " + event.getTitle()+" [ "+event.getProjectId()+" ]"
        );

        try {
            complianceClient.create(request);
            log.info("Compliance created for projectId={}", event.getProjectId());
        } catch (Exception e) {
            log.warn(
                "Compliance creation failed AFTER COMMIT for projectId={}",
                event.getProjectId(),
                e
            );
        }
    }
}