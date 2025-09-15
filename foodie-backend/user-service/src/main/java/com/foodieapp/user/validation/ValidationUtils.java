package com.foodieapp.user.validation;

import com.foodieapp.user.dto.request.AddressRequest;
import com.foodieapp.user.exception.ValidationException;
import com.foodieapp.user.model.AddressType;
import com.foodieapp.user.util.ValidationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Utility class for common validation operations.
 */
@Component
public class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    // Reuse patterns from ValidationConstants
    private static final Pattern EMAIL_PATTERN = Pattern.compile(ValidationConstants.EMAIL_PATTERN);
    private static final Pattern PHONE_PATTERN = Pattern.compile(ValidationConstants.PHONE_PATTERN);
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(ValidationConstants.PASSWORD_PATTERN);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(ValidationConstants.USERNAME_PATTERN);
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(ValidationConstants.POSTAL_CODE_PATTERN);

    /**
     * Validates if a string is a valid email address
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if a string is a valid phone number
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * Validates if a string is a strong password
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates if a string is a valid username
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates if a string is a valid postal code
     */
    public boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.isEmpty()) {
            return false;
        }
        return POSTAL_CODE_PATTERN.matcher(postalCode).matches();
    }

    /**
     * Performs address validation based on address type
     */
    public void validateAddress(AddressRequest addressRequest) {
        if (addressRequest == null) {
            throw new ValidationException("Address cannot be null");
        }

        // Validate required fields
        if (!StringUtils.hasText(addressRequest.getStreetAddress())) {
            throw new ValidationException("Street address is required");
        }

        if (!StringUtils.hasText(addressRequest.getCity())) {
            throw new ValidationException("City is required");
        }

        if (!StringUtils.hasText(addressRequest.getState())) {
            throw new ValidationException("State is required");
        }

        if (!StringUtils.hasText(addressRequest.getPostalCode())) {
            throw new ValidationException("Postal code is required");
        }

        if (!isValidPostalCode(addressRequest.getPostalCode())) {
            throw new ValidationException(ValidationConstants.INVALID_POSTAL_CODE);
        }

        // Validate address type
        String addressType = addressRequest.getAddressType();
        if (!StringUtils.hasText(addressType)) {
            throw new ValidationException("Address type is required");
        }

        try {
            AddressType type = AddressType.fromString(addressType);

            // Additional validation based on address type
            if (type == AddressType.FRIEND || type == AddressType.FAMILY) {
                if (!StringUtils.hasText(addressRequest.getRecipientName())) {
                    throw new ValidationException("Recipient name is required for " + type + " address");
                }

                if (!StringUtils.hasText(addressRequest.getRecipientPhoneNumber())) {
                    throw new ValidationException("Recipient phone number is required for " + type + " address");
                }

                if (!isValidPhoneNumber(addressRequest.getRecipientPhoneNumber())) {
                    throw new ValidationException(ValidationConstants.INVALID_PHONE_MESSAGE);
                }
            }
        } catch (IllegalArgumentException e) {
            String validTypes = Arrays.stream(AddressType.values())
                    .map(Enum::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            throw new ValidationException("Invalid address type. Valid types are: " + validTypes);
        }
    }

    /**
     * Validates OTP format
     * Requirements: 4-8 digits
     */
    public boolean isValidOtp(String otp) {
        if (otp == null || otp.isEmpty()) {
            return false;
        }
        return otp.matches("^[0-9]{4,8}$");
    }

    /**
     * Validates that two passwords match
     */
    public boolean doPasswordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
