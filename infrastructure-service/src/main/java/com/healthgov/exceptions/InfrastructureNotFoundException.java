package com.healthgov.exceptions;

public class InfrastructureNotFoundException extends RuntimeException {
    public InfrastructureNotFoundException(String message) {
        super(message);
    }
}

