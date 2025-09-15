package com.foodieapp.order.exception;

/**
 * Centralized error codes for the order service.
 * This provides a consistent way to identify error types across the application.
 */
public enum ErrorCodes {
    // Validation errors (400)
    VALIDATION_ERROR("VALIDATION_001", "Invalid input data", 400),
    INVALID_REQUEST("VALIDATION_002", "Invalid request format", 400),

    // Resource errors (404)
    RESOURCE_NOT_FOUND("RESOURCE_001", "Requested resource not found", 404),
    ORDER_NOT_FOUND("RESOURCE_002", "Order not found", 404),
    CART_NOT_FOUND("RESOURCE_003", "Cart not found", 404),

    // Authorization errors (401/403)
    UNAUTHORIZED("AUTH_001", "Authentication required", 401),
    FORBIDDEN("AUTH_002", "Not authorized to perform this action", 403),

    // Business rule violations (422)
    INVALID_ORDER_STATE("BUSINESS_001", "Invalid order state for operation", 422),
    EMPTY_CART("BUSINESS_002", "Cannot create order from empty cart", 422),
    CART_ITEM_NOT_FOUND("BUSINESS_003", "Item not found in cart", 422),
    MIXED_RESTAURANT_ITEMS("BUSINESS_004", "Cart contains items from different restaurants", 422),
    INVALID_PAYMENT_METHOD("BUSINESS_005", "Invalid payment method", 422),

    // External service errors (502)
    USER_SERVICE_ERROR("EXTERNAL_001", "Error communicating with user service", 502),
    RESTAURANT_SERVICE_ERROR("EXTERNAL_002", "Error communicating with restaurant service", 502),
    NOTIFICATION_SERVICE_ERROR("EXTERNAL_003", "Error communicating with notification service", 502),
    PAYMENT_SERVICE_ERROR("EXTERNAL_004", "Error communicating with payment service", 502),

    // General errors (500)
    GENERAL_ERROR("GENERAL_001", "General service error", 500);

    private final String code;
    private final String defaultMessage;
    private final int httpStatus;

    ErrorCodes(String code, String defaultMessage, int httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
