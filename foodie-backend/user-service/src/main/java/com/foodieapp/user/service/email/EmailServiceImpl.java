package com.foodieapp.user.service.email;

import com.foodieapp.user.client.NotificationServiceClient;
import com.foodieapp.user.dto.response.NotificationResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

private final NotificationServiceClient notificationClient;

@Value("${app.frontend.url}")
private String frontendUrl;

@Autowired
public EmailServiceImpl(NotificationServiceClient notificationClient) {
        this.notificationClient = notificationClient;
}

@Override
public NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp) {
        logger.info("Requesting password reset email for: {}", email);
        return notificationClient.sendPasswordResetEmail(email, token, otp);
}

@Override
public NotificationResponseDTO sendPasswordResetLink(String email, String token) {
        logger.info("Sending password reset email to: {}", email);

        // Using a Map to pass parameters to the notification service instead of hardcoding HTML
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", email);
        emailData.put("subject", "Reset Your Password - Foodie App");
        emailData.put("token", token);
        emailData.put("frontendUrl", frontendUrl);
        emailData.put("template", "password-reset");

        return notificationClient.sendEmail(emailData);
}

@Override
public NotificationResponseDTO sendEmailOtp(String email, String otp, String subject, String message) {
        logger.info("Requesting OTP email for: {}", email);
        return notificationClient.sendOtpEmail(email, otp, subject, message);
}

@Override
public NotificationResponseDTO sendVerificationEmail(String email, String token, String userId) {
        logger.info("Sending verification email to: {}", email);

        // Using a Map to pass parameters to the notification service instead of hardcoding HTML
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("to", email);
        emailData.put("subject", "Verify Your Email - Foodie App");
        emailData.put("token", token);
        emailData.put("userId", userId);
        emailData.put("frontendUrl", frontendUrl);
        emailData.put("template", "email-verification");

        return notificationClient.sendEmail(emailData);
}
}
