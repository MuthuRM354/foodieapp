package com.foodieapp.user.exception;

import com.foodieapp.user.dto.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), "NOT_FOUND"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        logger.error("Unauthorized: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), "UNAUTHORIZED"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Void>> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        logger.error("Token expired: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), "TOKEN_EXPIRED"), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error("You don't have permission to access this resource", "FORBIDDEN"),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        logger.error("Validation error: {}", errors);

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                "Validation failed",
                errors,
                new ApiResponse.ErrorDetails("VALIDATION_ERROR", "The request contains invalid data")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldPath = violation.getPropertyPath().toString();
            String fieldName = fieldPath.substring(fieldPath.lastIndexOf('.') + 1);
            String message = violation.getMessage();
            errors.put(fieldName, message);
        });

        logger.error("Constraint violation: {}", errors);

        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false,
                "Validation failed",
                errors,
                new ApiResponse.ErrorDetails("CONSTRAINT_VIOLATION", "The request contains invalid data")
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<Void>> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        logger.error("User already exists: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), "USER_ALREADY_EXISTS"), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception ex, WebRequest request) {
        logger.error("Authentication failed: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error("Invalid username or password", "INVALID_CREDENTIALS"),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            ResourceAccessException.class,
            ConnectException.class,
            SocketTimeoutException.class,
            TimeoutException.class
    })
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ApiResponse<Void>> handleResourceAccessException(
            Exception ex, WebRequest request) {
        logger.error("External service unavailable: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("A required service is temporarily unavailable. Please try again later.", "SERVICE_UNAVAILABLE"),
                HttpStatus.SERVICE_UNAVAILABLE
        );
    }

    @ExceptionHandler({
            DataAccessException.class,
            SQLException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleDatabaseException(Exception ex, WebRequest request) {
        logger.error("Database error: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("A database error occurred. Please try again later.", "DATABASE_ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleIOException(IOException ex, WebRequest request) {
        logger.error("IO error: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error("An IO error occurred. Please try again later.", "IO_ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(ValidationException ex, WebRequest request) {
        logger.error("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), "VALIDATION_ERROR"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRuleException(
            BusinessRuleException ex, WebRequest request) {
        logger.error("Business rule violation: {}", ex.getMessage());
        return new ResponseEntity<>(
                ApiResponse.error(ex.getMessage(), ex.getErrorCode()),
                HttpStatus.CONFLICT
        );
    }

    // Catch all other exceptions not specifically handled above
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        // Don't log full stack trace for expected exceptions
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            logger.error("Application error: {}", ex.getMessage());
        } else {
            logger.error("Internal server error: ", ex);
        }

        return new ResponseEntity<>(
                ApiResponse.error("An unexpected error occurred. Our team has been notified.", "INTERNAL_SERVER_ERROR"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
