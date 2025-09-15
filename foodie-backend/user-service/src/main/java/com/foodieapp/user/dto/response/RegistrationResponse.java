package com.foodieapp.user.dto.response;

public class RegistrationResponse {
    private String userId;
    private String email;
    private String phoneNumber;
    private String username;
    private String fullName;
    private String verificationType;
    private String verificationToken;
    private String accessToken;
    private String refreshToken;
    private String role;

    public RegistrationResponse() {
    }

    public RegistrationResponse(String userId, String email, String phoneNumber, String username,
                               String fullName, String verificationType, String verificationToken,
                               String accessToken, String refreshToken, String role) {
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.fullName = fullName;
        this.verificationType = verificationType;
        this.verificationToken = verificationToken;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}