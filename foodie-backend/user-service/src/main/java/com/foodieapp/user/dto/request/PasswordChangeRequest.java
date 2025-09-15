package com.foodieapp.user.dto.request;

import com.foodieapp.user.util.ValidationConstants;
import com.foodieapp.user.util.ValidationConstants.ValidPassword;
import com.foodieapp.user.validation.ValidationGroups.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeRequest {

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    private String currentPassword;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = ValidationConstants.PASSWORD_LENGTH_MESSAGE)
    private String newPassword;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = ValidationConstants.PASSWORD_LENGTH_MESSAGE)
    private String confirmPassword;

    // Default constructor
    public PasswordChangeRequest() {
    }

    // All args constructor
    public PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getters and setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}