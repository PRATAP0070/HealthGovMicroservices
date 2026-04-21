package com.healthgov.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CitizenNotFoundException extends RuntimeException {
	public CitizenNotFoundException(String message) {
		super(message);
	}

	public CitizenNotFoundException(Long id) {
		super("Citizen record not found with ID: " + id);
	}
}