package com.foodieapp.order.exception;

/**
 * Exception for resources that cannot be found.
 */
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final String resourceId;
    private final String errorCode;

    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;

        // Determine specific error code based on resource type
        if ("Order".equals(resourceType)) {
            this.errorCode = ErrorCodes.ORDER_NOT_FOUND.getCode();
        } else if ("Cart".equals(resourceType)) {
            this.errorCode = ErrorCodes.CART_NOT_FOUND.getCode();
        } else {
            this.errorCode = ErrorCodes.RESOURCE_NOT_FOUND.getCode();
        }
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
