package com.foodieapp.user.dto.request;

import com.foodieapp.user.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class OtpRequest {
    @Email(message = ValidationConstants.INVALID_EMAIL_MESSAGE)
    private String email;

    @Pattern(regexp = ValidationConstants.PHONE_PATTERN, message = ValidationConstants.INVALID_PHONE_MESSAGE)
    private String phoneNumber;

    // Method can be "email" or "phone"
    private String method;

    // For verification requests
    private String otp;

    // For verification: "email" or "phone"
    private String type;

    // For registration verification
    private String userId;

    // Type of verification: "registration", "password-reset", "login", etc.
    private String verificationType;

    // Token for operations that require a token
    private String verificationToken;

    // Is this a verification request
    private boolean verification = false;

    public OtpRequest() {
    }

    // Constructor for sending OTP
    public OtpRequest(String email, String phoneNumber, String method) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.method = method;
        this.verification = false;
    }

    // Constructor for verification
    public OtpRequest(String email, String phoneNumber, String otp, String type) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.type = type;
        this.verification = true;
    }

    // Constructor for registration verification
    public OtpRequest(String userId, String otp, String verificationType, boolean verification) {
        this.userId = userId;
        this.otp = otp;
        this.verificationType = verificationType;
        this.verification = verification;
    }

    // Full constructor
    public OtpRequest(String email, String phoneNumber, String method, String otp,
                      String type, String userId, String verificationType,
                      String verificationToken, boolean verification) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.method = method;
        this.otp = otp;
        this.type = type;
        this.userId = userId;
        this.verificationType = verificationType;
        this.verificationToken = verificationToken;
        this.verification = verification;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }
}
