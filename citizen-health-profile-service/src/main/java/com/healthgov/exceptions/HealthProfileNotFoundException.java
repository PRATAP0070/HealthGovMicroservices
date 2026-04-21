package com.healthgov.exceptions;

public class HealthProfileNotFoundException extends RuntimeException {
	public HealthProfileNotFoundException(Long id) {
		super("Health Profile not found for Citizen ID: " + id);
	}
}