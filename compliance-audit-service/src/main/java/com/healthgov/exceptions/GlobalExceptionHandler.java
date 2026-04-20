package com.healthgov.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.healthgov.dtos.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.NOT_FOUND.value())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase()).code("TARGET_NOT_FOUND").message(ex.getMessage()) // id=1"
				.path(req.getRequestURI()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(ComplianceRequestException.class)
	public ResponseEntity<ApiError> handleBadRequest(ComplianceRequestException ex, HttpServletRequest req) {
		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).code("BAD_REQUEST").message(ex.getMessage())
				.path(req.getRequestURI()).build();
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(AuditRequestException.class)
	public ResponseEntity<ApiError> handleAuditBadRequest(AuditRequestException ex, HttpServletRequest req) {
		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).code("BAD_REQUEST").message(ex.getMessage())
				.path(req.getRequestURI()).build();
		return ResponseEntity.badRequest().body(body);
	}
	
	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ApiError> handleTransactionException(
	        TransactionSystemException ex,
	        HttpServletRequest req) {

	    Throwable rootCause = ex.getMostSpecificCause();

	    if (rootCause instanceof AuditRequestException auditEx) {
	        ApiError body = ApiError.builder()
	                .timestamp(Instant.now())
	                .status(HttpStatus.BAD_REQUEST.value())
	                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
	                .code("BAD_REQUEST")
	                .message(auditEx.getMessage())
	                .path(req.getRequestURI())
	                .build();
	        return ResponseEntity.badRequest().body(body);
	    }

	    ApiError body = ApiError.builder()
	            .timestamp(Instant.now())
	            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
	            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
	            .code("INTERNAL_ERROR")
	            .message("Something went wrong. Please try again or contact support.")
	            .path(req.getRequestURI())
	            .build();
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
		String param = ex.getName();
		Object value = ex.getValue();

		String message;
		if ("type".equals(param) && ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
			message = "Invalid compliance type: '" + value + "'. Allowed values: PROGRAM, PROJECT, GRANT.";
		} else {
			message = "Invalid value for '" + param + "': '" + value + "'.";
		}

		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase()).code("INVALID_PARAMETER").message(message)
				.path(req.getRequestURI()).build();

		return ResponseEntity.badRequest().body(body);
	}
	 @ExceptionHandler(HttpMessageNotReadableException.class)
	    public ResponseEntity<Map<String, Object>> handleInvalidEnumValue(
	            HttpMessageNotReadableException ex) {

	        Throwable cause = ex.getCause();

	        if (cause instanceof InvalidFormatException ife &&
	                ife.getTargetType().isEnum()) {

	            String invalidValue = ife.getValue().toString();
	            String enumName = ife.getTargetType().getSimpleName();

	            Object[] allowedValues = ife.getTargetType().getEnumConstants();

	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(Map.of(
	                            "status", 400,
	                            "error", "Invalid value provided",
	                            "message", String.format(
	                                    "Invalid %s value '%s'. Allowed values are: %s",
	                                    enumName,
	                                    invalidValue,
	                                    java.util.Arrays.toString(allowedValues)
	                            )
	                    ));
	        }

	        // fallback
	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(Map.of(
	                        "status", 400,
	                        "error", "Malformed request",
	                        "message", "Request body or path variable is invalid"
	                ));
	    }


	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiError> handleConstraint(DataIntegrityViolationException ex, HttpServletRequest req) {
		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.CONFLICT.value())
				.error(HttpStatus.CONFLICT.getReasonPhrase()).code("DATA_INTEGRITY_VIOLATION")
				.message("Operation violates data constraints.").path(req.getRequestURI()).build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
		// Log ex with stack trace to your logger, but do NOT expose to clients
		ApiError body = ApiError.builder().timestamp(Instant.now()).status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).code("INTERNAL_ERROR")
				.message("Something went wrong. Please try again or contact support.").path(req.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}

}