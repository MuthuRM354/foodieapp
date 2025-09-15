package com.foodieapp.order.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

/**
 * Centralized validation constants and annotations for the order service.
 * This provides consistent validation across DTOs and eliminates duplication.
 */
public class ValidationConstants {
    // General validation messages
    public static final String USER_ID_REQUIRED = "User ID is required";
    public static final String RESTAURANT_ID_REQUIRED = "Restaurant ID is required";
    public static final String ITEM_ID_REQUIRED = "Item ID is required";
    public static final String NAME_REQUIRED = "Name is required";
    public static final String NAME_SIZE = "Name must be between 2 and 100 characters";
    public static final String PHONE_REQUIRED = "Phone number is required";
    public static final String PHONE_PATTERN = "^\\+?[1-9]\\d{1,14}$";
    public static final String PHONE_INVALID = "Invalid phone number format";
    public static final String ADDRESS_REQUIRED = "Address is required";
    public static final String ADDRESS_SIZE = "Address cannot exceed 255 characters";
    public static final String INSTRUCTIONS_SIZE = "Special instructions cannot exceed 500 characters";
    public static final String QUANTITY_POSITIVE = "Quantity must be at least 1";
    public static final String PRICE_POSITIVE = "Price cannot be negative";
    public static final String PAYMENT_METHOD_REQUIRED = "Payment method is required";

    // Length constraints
    public static final int MAX_INSTRUCTIONS_LENGTH = 500;
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_ADDRESS_LENGTH = 255;
    public static final int MIN_QUANTITY = 1;

    // Custom validation annotations
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = USER_ID_REQUIRED)
    @Documented
    public @interface UserId {
        String message() default USER_ID_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = RESTAURANT_ID_REQUIRED)
    @Documented
    public @interface RestaurantId {
        String message() default RESTAURANT_ID_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = NAME_REQUIRED)
    @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = NAME_SIZE)
    @Documented
    public @interface CustomerName {
        String message() default NAME_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = PHONE_REQUIRED)
    @Pattern(regexp = PHONE_PATTERN, message = PHONE_INVALID)
    @Documented
    public @interface PhoneNumber {
        String message() default PHONE_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = ADDRESS_REQUIRED)
    @Size(max = MAX_ADDRESS_LENGTH, message = ADDRESS_SIZE)
    @Documented
    public @interface Address {
        String message() default ADDRESS_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @Size(max = MAX_INSTRUCTIONS_LENGTH, message = INSTRUCTIONS_SIZE)
    @Documented
    public @interface SpecialInstructions {
        String message() default INSTRUCTIONS_SIZE;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = {})
    @NotBlank(message = PAYMENT_METHOD_REQUIRED)
    @Documented
    public @interface PaymentMethod {
        String message() default PAYMENT_METHOD_REQUIRED;
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
