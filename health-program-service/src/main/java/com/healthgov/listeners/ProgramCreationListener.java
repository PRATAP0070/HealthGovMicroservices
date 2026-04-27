package com.healthgov.listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.healthgov.citizenFeignClient.ComplianceClient;
import com.healthgov.dto.ComplianceCreateRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.events.ProgramCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProgramCreationListener {
	private final ComplianceClient complianceClient;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ProgramCreatedEvent event) {

		log.info("Running Project Complinace Listener");

		ComplianceCreateRequest request = new ComplianceCreateRequest();
		request.setType(ComplianceType.PROGRAM);
		request.setEntityId(event.getProgramId());
		request.setResult(ComplianceResult.UNDER_REVIEW.name());
		request.setNotes("Compliance created for Program: " + event.getTitle() + " [ " + event.getProgramId() + " ]");

		try {
			complianceClient.create(request);
			log.info("Compliance created for Healthcare program {} :- {}", event.getProgramId(),event.getTitle());
		} catch (Exception e) {
			log.warn("Compliance creation failed AFTER COMMIT for projectId={}", event.getProgramId(), e);
		}
	}
}
