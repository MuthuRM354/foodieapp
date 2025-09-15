package com.foodieapp.notification.service;

import com.foodieapp.notification.dto.NotificationResponseDTO;

import java.util.Map;

public interface EmailService {
    /**
     * Send a basic email
     */
    NotificationResponseDTO sendEmail(String to, String subject, String message, boolean isHtml);

    /**
     * Send an email using a map of attributes
     */
    NotificationResponseDTO sendEmail(Map<String, Object> emailData);

    /**
     * Send password reset email with token and OTP
     */
    NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp);

    /**
     * Send OTP email for verification
     */
    NotificationResponseDTO sendEmailOtp(String email, String otp, String subject, String message);

    /**
     * Send welcome email to new user
     */
    NotificationResponseDTO sendWelcomeEmail(String email, String name);
}
