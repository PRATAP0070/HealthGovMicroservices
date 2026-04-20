package com.healthgov.exceptions;

public class DuplicateCitizenException extends RuntimeException {
	public DuplicateCitizenException(String message) {
		super(message);
	}
}
