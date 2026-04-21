package com.healthgov.exceptions;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CitizenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(CitizenNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found",
                ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Bad Request", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobal(Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error", 
            ex.getMessage(), 
            request.getDescription(false)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Database Conflict",
                "Profile already exists or data integrity violated.",
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}