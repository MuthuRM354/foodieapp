package com.foodieapp.user.validation;

/**
 * Interface defining validation groups for different contexts.
 * Used to specify which validation rules should be applied in different scenarios.
 */
public interface ValidationGroups {

    /**
     * Validation group for creation operations.
     * Used when creating new entities.
     */
    interface Create {}

    /**
     * Validation group for update operations.
     * Used when updating existing entities.
     */
    interface Update {}

    /**
     * Validation group for deletion operations.
     * Used when deleting entities.
     */
    interface Delete {}

    /**
     * Validation group for sign-in operations.
     * Used for authentication requests.
     */
    interface SignIn {}

    /**
     * Validation group for registration operations.
     * Used for user registration.
     */
    interface Register {}

    /**
     * Validation group for password operations.
     * Used for password change or reset.
     */
    interface Password {}

    /**
     * Validation group for OTP verification operations.
     * Used for OTP verification process.
     */
    interface OtpVerification {}

    /**
     * Validation group for admin operations.
     * Used for operations that require admin privileges.
     */
    interface Admin {}
}
