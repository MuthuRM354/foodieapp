package com.foodieapp.user.service.email;

import com.foodieapp.user.dto.response.NotificationResponseDTO;

public interface EmailService {
    /**
     * Send password reset email with token and OTP
     * @return Notification response with status
     */
    NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp);

    /**
     * Send OTP email for verification
     * @return Notification response with status
     */
    NotificationResponseDTO sendEmailOtp(String email, String otp, String subject, String message);

    /**
     * Send verification email with validation link
     * @return Notification response with status
     */
    NotificationResponseDTO sendVerificationEmail(String email, String token, String userId);

    /**
     * Send password reset link
     * @return Notification response with status
     */
    NotificationResponseDTO sendPasswordResetLink(String email, String token);
}