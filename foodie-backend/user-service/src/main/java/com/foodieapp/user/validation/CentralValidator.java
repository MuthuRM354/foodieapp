package com.foodieapp.user.validation;

import com.foodieapp.user.dto.request.AddressRequest;
import com.foodieapp.user.exception.ValidationException;
import com.foodieapp.user.model.AddressType;
import com.foodieapp.user.util.ValidationConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Centralized validator service that uses the constants from ValidationConstants
 * to provide consistent validation across the application.
 */
@Component
public class CentralValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(ValidationConstants.EMAIL_PATTERN);
    private static final Pattern PHONE_PATTERN = Pattern.compile(ValidationConstants.PHONE_PATTERN);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(ValidationConstants.PASSWORD_PATTERN);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(ValidationConstants.USERNAME_PATTERN);
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(ValidationConstants.POSTAL_CODE_PATTERN);
    private static final Pattern TIME_PATTERN = Pattern.compile(ValidationConstants.TIME_PATTERN);

    /**
     * Validates email format
     */
    public void validateEmail(String email) {
        if (email == null || email.isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(ValidationConstants.INVALID_EMAIL_MESSAGE);
        }
    }

    /**
     * Validates phone number format
     */
    public void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty() || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException(ValidationConstants.INVALID_PHONE_MESSAGE);
        }
    }

    /**
     * Validates password strength
     */
    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new ValidationException(ValidationConstants.REQUIRED_FIELD_MESSAGE);
        }

        if (password.length() < ValidationConstants.PASSWORD_MIN_LENGTH) {
            throw new ValidationException(ValidationConstants.PASSWORD_LENGTH_MESSAGE);
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException(ValidationConstants.PASSWORD_COMPLEXITY_MESSAGE);
        }
    }

    /**
     * Validates username format
     */
    public void validateUsername(String username) {
        if (username == null || username.isEmpty()) {
            throw new ValidationException(ValidationConstants.REQUIRED_FIELD_MESSAGE);
        }

        if (username.length() < ValidationConstants.USERNAME_MIN_LENGTH ||
            username.length() > ValidationConstants.USERNAME_MAX_LENGTH) {
            throw new ValidationException(ValidationConstants.USERNAME_LENGTH_MESSAGE);
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new ValidationException("Username can only contain letters, numbers, underscores, and hyphens");
        }
    }

    /**
     * Validates postal code format
     */
    public void validatePostalCode(String postalCode) {
        if (postalCode == null || postalCode.isEmpty() || !POSTAL_CODE_PATTERN.matcher(postalCode).matches()) {
            throw new ValidationException(ValidationConstants.INVALID_POSTAL_CODE);
        }
    }

    /**
     * Validates time format (HH:mm)
     */
    public void validateTimeFormat(String time) {
        if (time == null || time.isEmpty() || !TIME_PATTERN.matcher(time).matches()) {
            throw new ValidationException(ValidationConstants.INVALID_TIME_FORMAT);
        }
    }

    /**
     * Comprehensive address validation
     */
    public void validateAddress(AddressRequest address) {
        if (address == null) {
            throw new ValidationException("Address cannot be null");
        }

        if (!StringUtils.hasText(address.getStreetAddress())) {
            throw new ValidationException("Street address is required");
        }

        if (!StringUtils.hasText(address.getCity())) {
            throw new ValidationException("City is required");
        }

        if (!StringUtils.hasText(address.getState())) {
            throw new ValidationException("State is required");
        }

        if (!StringUtils.hasText(address.getPostalCode())) {
            throw new ValidationException("Postal code is required");
        }

        validatePostalCode(address.getPostalCode());

        if (!StringUtils.hasText(address.getAddressType())) {
            throw new ValidationException("Address type is required");
        }

        try {
            AddressType type = AddressType.fromString(address.getAddressType());

            // Additional validation for FRIEND or FAMILY addresses
            if (type == AddressType.FRIEND || type == AddressType.FAMILY) {
                if (!StringUtils.hasText(address.getRecipientName())) {
                    throw new ValidationException("Recipient name is required for " + type + " address");
                }

                if (!StringUtils.hasText(address.getRecipientPhoneNumber())) {
                    throw new ValidationException("Recipient phone number is required for " + type + " address");
                }

                if (address.getRecipientPhoneNumber() != null &&
                    !PHONE_PATTERN.matcher(address.getRecipientPhoneNumber()).matches()) {
                    throw new ValidationException(ValidationConstants.INVALID_PHONE_MESSAGE);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}