package com.foodieapp.order.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the order service.
 * This provides consistent error responses for all exceptions.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        logger.warn("Resource not found: {}", ex.getMessage());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.NOT_FOUND,
            "Not Found",
            ex.getMessage(),
            "RESOURCE_NOT_FOUND",
            request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<Map<String, Object>> handleOrderServiceException(OrderServiceException ex, WebRequest request) {
        logger.warn("Order service exception: {}", ex.getMessage());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.valueOf(ex.getHttpStatus()),
            "Order Service Error",
            ex.getMessage(),
            ex.getErrorCode(),
            request.getDescription(false)
        );

        if (ex.getDetails() != null) {
            body.put("details", ex.getDetails());
        }

        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        logger.warn("Unauthorized access: {}", ex.getMessage());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "Unauthorized",
            ex.getMessage(),
            "UNAUTHORIZED",
            request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex, WebRequest request) {
        logger.warn("Illegal state: {}", ex.getMessage());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage(),
            "INVALID_STATE",
            request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        logger.warn("Illegal argument: {}", ex.getMessage());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Bad Request",
            ex.getMessage(),
            "INVALID_ARGUMENT",
            request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Validation error: {}", ex.getBindingResult().getAllErrors());

        Map<String, Object> body = createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Validation Error",
            "Input validation failed",
            "VALIDATION_ERROR",
            request.getDescription(false)
        );

        // Group validation errors by field
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage,
                    // If multiple errors for the same field, concatenate them
                    (error1, error2) -> error1 + "; " + error2
                ));

        body.put("fieldErrors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        Map<String, Object> body = createErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred",
            "GENERAL_ERROR",
            request.getDescription(false)
        );

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper method to create standardized error responses
     */
    private Map<String, Object> createErrorResponse(HttpStatus status, String error,
                                                   String message, String errorCode,
                                                   String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("errorCode", errorCode);
        body.put("path", path);
        return body;
    }
}
