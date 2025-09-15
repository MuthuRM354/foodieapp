package com.foodieapp.user.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum AddressType {
    HOME,
    WORK,
    FRIEND,
    FAMILY,
    OTHER;

    /**
     * Check if a string is a valid address type
     */
    public static boolean isValid(String type) {
        if (type == null) return false;
        try {
            valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Convert string to AddressType enum
     */
    public static AddressType fromString(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Address type cannot be null");
        }
        try {
            return valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            String validTypes = Arrays.stream(values())
                    .map(AddressType::name)
                    .collect(Collectors.joining(", "));

            throw new IllegalArgumentException(
                    "Invalid address type: " + type + ". Valid types are: " + validTypes);
        }
    }
}