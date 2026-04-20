package com.healthgov.exceptions;

public class ProgramNotFoundException extends RuntimeException {

    public ProgramNotFoundException(String message) {
        super(message);
    }
}