package com.healthgov.exceptions;

import org.springframework.http.HttpStatus;

public class ProgramException extends RuntimeException {

    private final HttpStatus status;

    public ProgramException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }

    public static ProgramException notFound(String msg) { return new ProgramException(msg, HttpStatus.NOT_FOUND); }
    public static ProgramException badRequest(String msg) { return new ProgramException(msg, HttpStatus.BAD_REQUEST); }
    public static ProgramException conflict(String msg) { return new ProgramException(msg, HttpStatus.CONFLICT); }
}