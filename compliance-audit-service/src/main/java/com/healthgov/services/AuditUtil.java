package com.healthgov.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.healthgov.exceptions.AuditRequestException;
import com.healthgov.exceptions.ResourceNotFoundException;
import com.healthgov.feignclients.ProgramClient;
import com.healthgov.feignclients.ProjectClient;

import feign.FeignException.FeignClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditUtil {
	private static final Set<String> ALLOWED_SCOPE_TYPES = Set.of("PROGRAM", "PROJECT", "GRANT");

	private final ProgramClient programClient;
	private final ProjectClient projectClient;
	
	public void validateAndEnsureScopeTargetExists(String scope) {

		String[] parts = scope.split(":", 2);
		if (parts.length != 2) {
			throw new AuditRequestException("Invalid scope format. Use PROGRAM:<id>, PROJECT:<id>, or GRANT:<id>.");
		}

		String type = parts[0].trim().toUpperCase();
		String idPart = parts[1].trim();

		if (!ALLOWED_SCOPE_TYPES.contains(type)) {
			throw new AuditRequestException("Invalid scope type. Allowed: PROGRAM, PROJECT, GRANT.");
		}

		long id;
		try {
			id = Long.parseLong(idPart);
		} catch (NumberFormatException ex) {
			throw new AuditRequestException("Invalid scope id. Use numeric id. Example: PROGRAM:4");
		}

		Boolean exists;
		try {

			switch (type) {

			case "PROGRAM" -> {
				log.debug("Calling ProgramClient.programExists(id={})", id);
				exists = programClient.programExists(id);
				log.debug("ProgramClient response for id {}: {}", id, exists);
			}

			case "PROJECT" -> {
				log.debug("Calling ProjectClient.projectExists(id={})", id);
				exists = projectClient.projectExists(id);
				log.debug("ProjectClient response for id {}: {}", id, exists);
			}

			case "GRANT" -> {
				log.debug("Calling ProjectClient.grantExists(id={})", id);
				exists = projectClient.grantExists(id);
				log.debug("Grant existence response for id {}: {}", id, exists);
			}

			default -> {
				log.error("Unexpected scope type encountered: {}", type);
				exists = false;
			}
			}
		} catch (FeignClientException e) {
			throw new AuditRequestException("Unable to validate scope. Dependent service unavailable.");
		}

		if (exists == null || !exists) {
			throw new ResourceNotFoundException("Scope target not found: " + type + " id=" + id);
		}
	}
}
