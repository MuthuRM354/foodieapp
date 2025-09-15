package com.foodieapp.notification.service;

import com.foodieapp.notification.dto.EmailNotificationDTO;
import com.foodieapp.notification.dto.NotificationResponseDTO;
import com.foodieapp.notification.dto.SmsNotificationDTO;

public interface NotificationService {
    NotificationResponseDTO sendEmailNotification(EmailNotificationDTO email);
    NotificationResponseDTO sendSmsNotification(SmsNotificationDTO sms);
    NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp);
    NotificationResponseDTO sendOtpEmail(String email, String otp, String subject, String message);

    // Add the new methods
    NotificationResponseDTO sendWelcomeEmail(String email, String name);
    NotificationResponseDTO sendVerificationEmail(String email, String token, String userId);
}