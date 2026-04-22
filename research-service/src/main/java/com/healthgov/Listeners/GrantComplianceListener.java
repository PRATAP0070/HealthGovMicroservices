package com.healthgov.Listeners;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.healthgov.client.ComplianceClient;
import com.healthgov.dto.ComplianceCreateRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.events.GrantCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrantComplianceListener {
	private final ComplianceClient complianceClient;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onGrantCreated(GrantCreatedEvent event) {
		log.info("Compliance is Creating for teh Grant");

		ComplianceCreateRequest request = new ComplianceCreateRequest();
		request.setType(ComplianceType.GRANT);
		request.setEntityId(event.getGrantId());
		request.setResult(ComplianceResult.UNDER_REVIEW.name());
		request.setNotes(
				"Compliance created for Grant ID " + event.getGrantId() + " (Project ID " + event.getProjectId() + ")");

		try {
			complianceClient.create(request);
			log.info("Compliance created for Grant {}", event.getGrantId());
		} catch (Exception e) {
			log.warn("Compliance creation failed for Grant {}, will retry later", event.getGrantId(), e);
		}
	}

}
