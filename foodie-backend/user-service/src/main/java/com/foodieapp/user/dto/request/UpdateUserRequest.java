package com.foodieapp.user.dto.request;

import com.foodieapp.user.util.ValidationConstants;
import com.foodieapp.user.validation.ValidationGroups.Update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Size(min = ValidationConstants.FULLNAME_MIN_LENGTH, max = ValidationConstants.FULLNAME_MAX_LENGTH,
          message = ValidationConstants.FULLNAME_LENGTH_MESSAGE)
    private String fullName;

    private String profilePictureUrl;

    // Default constructor
    public UpdateUserRequest() {
    }

    // All args constructor
    public UpdateUserRequest(String fullName, String profilePictureUrl) {
        this.fullName = fullName;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}