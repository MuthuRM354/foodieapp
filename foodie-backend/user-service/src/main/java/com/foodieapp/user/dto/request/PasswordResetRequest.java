package com.foodieapp.user.dto.request;

import com.foodieapp.user.util.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class PasswordResetRequest {
    @Email(message = ValidationConstants.INVALID_EMAIL_MESSAGE,
            groups = InitialReset.class)
    private String email;

    private String token;
    private String otp;

    @Size(min = 8, message = ValidationConstants.PASSWORD_LENGTH_MESSAGE,
            groups = {Verification.class, Reset.class})
    private String newPassword;

    @Size(min = 8, message = ValidationConstants.PASSWORD_LENGTH_MESSAGE,
            groups = Reset.class)
    private String confirmPassword;

    private boolean verification = false;

    // Validation groups
    public interface InitialReset {}
    public interface Verification {}
    public interface Reset {}

    public PasswordResetRequest() {
    }

    // Constructor for initial reset request
    public PasswordResetRequest(String email) {
        this.email = email;
        this.verification = false;
    }

    // Constructor for OTP verification and password reset (original flow)
    public PasswordResetRequest(String token, String otp, String newPassword) {
        this.token = token;
        this.otp = otp;
        this.newPassword = newPassword;
        this.verification = true;
    }

    // Constructor for token-based reset (new flow)
    // Change signature to avoid duplicate constructor
    public PasswordResetRequest(String token, String newPassword, String confirmPassword, boolean useTokenReset) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.verification = true;
    }

    // Full constructor
    public PasswordResetRequest(String email, String token, String otp, String newPassword,
                                String confirmPassword, boolean verification) {
        this.email = email;
        this.token = token;
        this.otp = otp;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
        this.verification = verification;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public boolean isVerification() { return verification; }
    public void setVerification(boolean verification) { this.verification = verification; }
}
