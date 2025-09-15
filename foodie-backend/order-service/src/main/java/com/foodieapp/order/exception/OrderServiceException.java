package com.foodieapp.order.exception;

/**
 * Standard exception for order service business logic errors.
 * This provides consistent error handling throughout the application.
 */
public class OrderServiceException extends RuntimeException {
    private final ErrorCodes errorCode;
    private final String details;

    public OrderServiceException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public OrderServiceException(String message, ErrorCodes errorCode, String details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public OrderServiceException(String message, ErrorCodes errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.details = null;
    }

    public String getErrorCode() {
        return errorCode.getCode();
    }

    public int getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getDetails() {
        return details;
    }

    public ErrorCodes getErrorCodeEnum() {
        return errorCode;
    }
}