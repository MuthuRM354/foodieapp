package com.foodieapp.user.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

/**
 * Centralized constants for validation patterns and messages
 */
public final class ValidationConstants {
    // Prevent instantiation
    private ValidationConstants() {}

    // Patterns
    public static final String PHONE_PATTERN = "^\\+?[0-9]{10,15}$";
    public static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,50}$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final String POSTAL_CODE_PATTERN = "^[0-9]{5,6}$";
    public static final String TIME_PATTERN = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

    // Min-Max Values
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int FULLNAME_MIN_LENGTH = 2;
    public static final int FULLNAME_MAX_LENGTH = 100;

    // Messages
    public static final String INVALID_EMAIL_MESSAGE = "Invalid email format";
    public static final String INVALID_PHONE_MESSAGE = "Phone number should be between 10 and 15 digits";
    public static final String PASSWORD_LENGTH_MESSAGE = "Password must be at least 8 characters long";
    public static final String PASSWORD_COMPLEXITY_MESSAGE = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character";
    public static final String REQUIRED_FIELD_MESSAGE = "This field is required";
    public static final String USERNAME_LENGTH_MESSAGE = "Username must be between 3 and 50 characters";
    public static final String FULLNAME_LENGTH_MESSAGE = "Full name must be between 2 and 100 characters";
    public static final String INVALID_TIME_FORMAT = "Time must be in format HH:mm";
    public static final String INVALID_POSTAL_CODE = "Postal code must be 5-6 digits";

    // Custom validation annotations
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @Pattern(regexp = PHONE_PATTERN, message = INVALID_PHONE_MESSAGE)
    public @interface ValidPhone {
        String message() default INVALID_PHONE_MESSAGE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @Pattern(regexp = USERNAME_PATTERN, message = USERNAME_LENGTH_MESSAGE)
    public @interface ValidUsername {
        String message() default USERNAME_LENGTH_MESSAGE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @Pattern(regexp = PASSWORD_PATTERN, message = PASSWORD_COMPLEXITY_MESSAGE)
    public @interface ValidPassword {
        String message() default PASSWORD_COMPLEXITY_MESSAGE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @Pattern(regexp = EMAIL_PATTERN, message = INVALID_EMAIL_MESSAGE)
    public @interface ValidEmail {
        String message() default INVALID_EMAIL_MESSAGE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
