package com.healthgov.exceptions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(InfrastructureNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleResourceNotFound(InfrastructureNotFoundException e) {

		log.error("Infrastructure not found exception occurred: {}", e.getMessage(), e);

		ExceptionResponse ex = new ExceptionResponse(e.getMessage(), LocalDate.now(), 404);

		return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ProgramNotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleProgramNotFound(ProgramNotFoundException e) {

		log.error("Program not found exception occurred: {}", e.getMessage(), e);

		ExceptionResponse ex = new ExceptionResponse(e.getMessage(), LocalDate.now(), 404);

		return new ResponseEntity<>(ex, HttpStatus.NOT_FOUND);
	}

	// Business rule violations based on current entity state
	// (e.g., program not ACTIVE, resource is COMPLETED or ACTIVE for delete)
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ExceptionResponse> handleIllegalState(IllegalStateException e) {

		log.error("Business rule violation: {}", e.getMessage());

		ExceptionResponse ex = new ExceptionResponse(e.getMessage(), LocalDate.now(), 400);

		return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
	}

	// Invalid client input values (e.g., negative quantity)
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ExceptionResponse> handleIllegalArgument(IllegalArgumentException e) {

		log.error("Invalid argument: {}", e.getMessage());

		ExceptionResponse ex = new ExceptionResponse(e.getMessage(), LocalDate.now(), 400);
		return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {

		log.warn("Validation failed for request: {}", ex.getMessage());

		StringBuilder errors = new StringBuilder();
		ex.getBindingResult().getFieldErrors().forEach(
				error -> errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));

		return new ResponseEntity<>("Validation error(s): " + errors, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatch(
	        MethodArgumentTypeMismatchException ex) {

	    String paramName = ex.getName();
	    Object invalidValue = ex.getValue();

	    String message;

	    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {

	        // Extract enum values
	        List<String> allowedValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
	                .map(Object::toString)
	                .collect(Collectors.toList());

	        message = String.format(
	                "Invalid value '%s' for parameter '%s'. Allowed values are %s",
	                invalidValue, paramName, allowedValues
	        );

	    } else {

	        String requiredType = ex.getRequiredType() != null
	                ? ex.getRequiredType().getSimpleName()
	                : "unknown";

	        message = String.format(
	                "Invalid value '%s' for parameter '%s'. Expected type: %s",
	                invalidValue, paramName, requiredType
	        );
	    }

	    log.error("Method argument type mismatch: {}", message);

	    ExceptionResponse response = new ExceptionResponse(
	            message,
	            LocalDate.now(),
	            400
	    );

	    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGenericException(Exception e) {

		log.error("Unhandled exception occurred", e);

		ExceptionResponse ex = new ExceptionResponse("Internal server error", LocalDate.now(), 500);

		return new ResponseEntity<>(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}