package com.healthgov.exceptions;

import org.springframework.http.HttpStatus;

public class MedicalResearchException extends RuntimeException {

    private final HttpStatus status;

    public MedicalResearchException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}