package com.healthgov.services;

import java.util.EnumSet;

import org.springframework.stereotype.Service;

import com.healthgov.enums.ComplianceResult;
import com.healthgov.enums.ComplianceType;
import com.healthgov.exceptions.ComplianceRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.feignclients.ProgramClient;
import com.healthgov.feignclients.ProjectClient;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComplianceUtil {
	private final ProgramClient programClient;
	private final ProjectClient projectClient;

	public ComplianceResult parseResultOrThrow(String result) {
		log.info("Validating the compliance result based on the enums");
		try {
			return ComplianceResult.valueOf(result.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new ComplianceRequestException(
					"Invalid result. Allowed: COMPLIANT, PARTIALLY_COMPLIANT, NON_COMPLIANT, UNDER_REVIEW.");
		}
	}

	public void validateTypeAndEntityId(ComplianceType type, Long entityId) {

		log.info("Validating the compliance type before updatring the status");
		if (type == null)
			throw new ComplianceRequestException("type is required (PROGRAM/PROJECT/GRANT).");
		if (entityId == null)
			throw new ComplianceRequestException("entityId is required.");

		if (!EnumSet.of(ComplianceType.PROGRAM, ComplianceType.PROJECT, ComplianceType.GRANT).contains(type)) {
			throw new ComplianceRequestException("Invalid type. Allowed: PROGRAM, PROJECT, GRANT.");
		}
	}

	public void ensureTargetExists(ComplianceType type, Long entityId) {

		log.info("Validating target entity: type={}, entityId={}", type, entityId);

		if (type == null || entityId == null) {
			throw new ComplianceRequestException("type and entityId are required.");
		}

		Boolean exists;

		try {
			exists = switch (type) {
			case PROGRAM -> programClient.programExists(entityId);
			case PROJECT -> projectClient.projectExists(entityId);
			case GRANT -> projectClient.grantExists(entityId);
			};
		} catch (FeignClientException e) {
			log.error("Feign validation failed for {}:{}", type, entityId, e);
			throw new ComplianceRequestException("Unable to validate target entity. Please try again later.");
		}

		if (exists == null || !exists) {
			throw new ResourceNotFoundException("Target not found: " + type + " id=" + entityId);
		}
	}

	public Object fetchEntityDetails(ComplianceType type, Long entityId) {

		try {
			return switch (type) {

			case PROGRAM -> {
				log.info("[PROGRAM-CLIENT] Calling getProgramById with id={}", entityId);
				Object program = programClient.getProgramById(entityId);
				log.info("[PROGRAM-CLIENT] Response received: {}", program);
				yield program;
			}

			case PROJECT -> {
				log.info("[PROJECT-CLIENT] Calling getProjectById with id={}", entityId);
				Object project = projectClient.getProjectById(entityId);
				log.info("[PROJECT-CLIENT] Response received: {}", project);
				yield project;
			}

			case GRANT -> {
				log.info("[GRANT-CLIENT] Calling getGrantById with id={}", entityId);
				Object grant = projectClient.getGrantById(entityId);
				log.info("[GRANT-CLIENT] Response received: {}", grant);
				yield grant;
			}
			};

		} catch (Exception e) {
			log.error("[ENTITY-FETCH-FAILED] type={} entityId={}", type, entityId, e);
			return "micro Service is Down";
		}
	}
}
