package com.healthgov.services;

import org.springframework.stereotype.Component;
import com.healthgov.dtos.ComplianceResponseDTO;
import com.healthgov.dtos.UniversalNotificationRequest;
import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.NotificationCategory;
import com.healthgov.feignclients.NotificationClient;
import com.healthgov.models.ComplianceRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationUtil {

	private final NotificationClient notificationClient;

	//Called after compliance is CREATED
	public void notifyOnCreate(ComplianceResponseDTO record) {

		String title = record.getEntity().getTitle();
		Long ownerId = record.getEntity().getOwnerId();

		String message = "New Compliance Record Created\n\n" + "Entity: " + title + "\n" + "Compliance ID: "
				+ record.getComplianceId() + "\n" + "Current Status: " + record.getResult() + "\n\n" + "Findings:\n"
				+ record.getNotes() + "\n\nRegards,\nHealthGov Compliance Team";

		UniversalNotificationRequest notification = new UniversalNotificationRequest();
		notification.setUserId(ownerId);
		notification.setEmail("vikki.vignesh6366@gmail.com");
		notification.setEntityId(record.getEntityId());
		notification.setCategory(NotificationCategory.COMPLIANCE);
		notification.setMessage(message);

		log.info("Compliance Created Notification {}",notification);
		notificationClient.sendUniversalNotification(notification);
	}

	//Called after compliance is UPDATED
	public void notifyAfterRecordUpdate(Long ownerId, String email,Long entityId, String message) {

		UniversalNotificationRequest notification = new UniversalNotificationRequest();
		notification.setUserId(ownerId);
		notification.setEntityId(entityId);
		notification.setEmail("vikki.vignesh6366@gmail.com");
		//notification.setEmail(email);
		notification.setCategory(NotificationCategory.COMPLIANCE);
		notification.setMessage(message);

		log.info("Compliance Updated Notification {}",notification);
		notificationClient.sendUniversalNotification(notification);
	}

	//Only builds message (pure function)
	public static String buildDetailedComplianceMessage(ComplianceRecord before, ComplianceRecord after,
			String officerName, String entityTitle) {

		StringBuilder sb = new StringBuilder();

		sb.append("Compliance Update Notification\n\n");
		sb.append("Entity: ").append(entityTitle).append("\n");
		sb.append("Compliance ID: ").append(after.getComplianceId()).append("\n");
		sb.append("Updated On: ").append(after.getDate()).append("\n");
		sb.append("Updated By: ").append(officerName).append("\n\n");

		sb.append("Change Summary:\n");

		if (!before.getResult().equals(after.getResult())) {
			sb.append("- Compliance Status changed from ").append(before.getResult()).append(" → ")
					.append(after.getResult()).append("\n");
		}

		if (!before.getNotes().equals(after.getNotes())) {
			sb.append("- Findings Updated:\n");
			sb.append("  Previous Findings: ").append(before.getNotes()).append("\n");
			sb.append("  Current Findings: ").append(after.getNotes()).append("\n");
		}

		sb.append("\nCompliance Interpretation:\n");
		sb.append(getResultExplanation(after.getResult()));

		sb.append("\n\nRequired Actions:\n");
		sb.append(getRequiredAction(after.getResult()));

		sb.append("\n\nRegards,\nHealthGov Compliance Team");

		return sb.toString();
	}

	private static String getResultExplanation(ComplianceResult result) {
		return switch (result) {
		case COMPLIANT -> "- All compliance requirements have been fully met.";
		case PARTIALLY_COMPLIANT -> "- The entity meets some compliance requirements, but gaps remain.";
		case NON_COMPLIANT -> "- Significant compliance violations have been identified.";
		case UNDER_REVIEW -> "- Compliance evaluation is currently in progress.";
		};
	}

	private static String getRequiredAction(ComplianceResult result) {
		return switch (result) {
		case COMPLIANT -> "- No further action required.\n- Continue standard monitoring and reporting.";
		case PARTIALLY_COMPLIANT ->
			"- Review the listed findings carefully.\n- Submit corrective actions for pending compliance gaps.";
		case NON_COMPLIANT ->
			"- Immediate corrective action is mandatory.\n- Address all violations and resubmit for review.";
		case UNDER_REVIEW ->
			"- Await completion of review process.\n- Be prepared to provide additional documents if requested.";
		};
	}
}