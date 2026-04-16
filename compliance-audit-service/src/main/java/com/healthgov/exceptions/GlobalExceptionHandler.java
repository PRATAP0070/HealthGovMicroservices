package com.healthgov.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

//	@ExceptionHandler(CitizenNotFoundException.class)
//	public ResponseEntity<ErrorResponse> handleNotFound(CitizenNotFoundException ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found",
//				ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//	}
//
//	@ExceptionHandler(MethodArgumentNotValidException.class)
//	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
//		String message = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				"Validation Failed", message, request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(HttpMessageNotReadableException.class)
//	public ResponseEntity<ErrorResponse> handleJsonError(HttpMessageNotReadableException ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				"Malformed Request", "Invalid value provided for one of the fields (check enums like Gender)",
//				request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//	}
//
//	// --- ADDED THIS NEW EXCEPTION HANDLER FOR REGISTRATION ---
//	@ExceptionHandler(IllegalArgumentException.class)
//	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
//				"Bad Request", ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//	}
//
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleGlobal(Exception ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
//				"Internal Server Error", "An unexpected error occurred", request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//
//	@ExceptionHandler(RuntimeException.class)
//	public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), "Not Found",
//				ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//	}
//
//	@ExceptionHandler(HealthProfileNotFoundException.class)
//	public ResponseEntity<ErrorResponse> handleProfileNotFound(HealthProfileNotFoundException ex, WebRequest request) {
//		ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
//				"Health Profile Not Found", ex.getMessage(), request.getDescription(false));
//		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//	}
}