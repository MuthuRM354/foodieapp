package com.foodieapp.user.dto.request;

import com.foodieapp.user.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateUserRequest {
    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Size(min = 3, max = 50, message = ValidationConstants.USERNAME_LENGTH_MESSAGE)
    private String username;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Email(message = ValidationConstants.INVALID_EMAIL_MESSAGE)
    private String email;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Size(min = 8, message = ValidationConstants.PASSWORD_LENGTH_MESSAGE)
    private String password;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    private String fullName;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Pattern(regexp = ValidationConstants.PHONE_PATTERN, message = ValidationConstants.INVALID_PHONE_MESSAGE)
    private String phoneNumber;

    @NotBlank(message = ValidationConstants.REQUIRED_FIELD_MESSAGE)
    @Pattern(regexp = "^(email|phone)$", message = "Verification method must be 'email' or 'phone'")
    private String verificationMethod;

    public CreateUserRequest() {
    }

    public CreateUserRequest(String username, String email, String password, String fullName, String phoneNumber, String verificationMethod) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.verificationMethod = verificationMethod;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }
}