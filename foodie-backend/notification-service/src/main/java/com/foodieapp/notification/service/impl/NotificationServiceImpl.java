package com.foodieapp.notification.service.impl;

import com.foodieapp.notification.dto.EmailNotificationDTO;
import com.foodieapp.notification.dto.NotificationResponseDTO;
import com.foodieapp.notification.dto.SmsNotificationDTO;
import com.foodieapp.notification.service.EmailService;
import com.foodieapp.notification.service.EmailTemplateService;
import com.foodieapp.notification.service.NotificationService;
import com.foodieapp.notification.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final EmailService emailService;
    private final SmsService smsService;
    private final EmailTemplateService templateService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public NotificationServiceImpl(EmailService emailService,
                                   SmsService smsService,
                                   EmailTemplateService templateService) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.templateService = templateService;
    }

    @Override
    public NotificationResponseDTO sendEmailNotification(EmailNotificationDTO email) {
        logger.info("Sending email notification to: {}", email.getTo());
        return emailService.sendEmail(
                email.getTo(),
                email.getSubject(),
                email.getBody() != null ? email.getBody() : email.getMessage(),
                email.isHtml()
        );
    }

    @Override
    public NotificationResponseDTO sendSmsNotification(SmsNotificationDTO sms) {
        logger.info("Sending SMS notification to: {}", sms.getTo());
        boolean sent = smsService.sendSms(sms.getTo(), sms.getMessage());

        return NotificationResponseDTO.builder()
                .success(sent)
                .message(sent ? "SMS notification sent successfully" : "Failed to send SMS notification")
                .recipient(sms.getTo())
                .status(sent ? "SENT" : "FAILED")
                .build();
    }

    @Override
    public NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp) {
        // Use EmailService directly to avoid duplicate templates
        return emailService.sendPasswordResetEmail(email, token, otp);
    }

    @Override
    public NotificationResponseDTO sendOtpEmail(String email, String otp, String subject, String message) {
        // Use EmailService directly
        return emailService.sendEmailOtp(email, otp, subject, message);
    }

    @Override
    public NotificationResponseDTO sendWelcomeEmail(String email, String name) {
        // Use EmailService directly
        return emailService.sendWelcomeEmail(email, name);
    }

    @Override
    public NotificationResponseDTO sendVerificationEmail(String email, String token, String userId) {
        logger.info("Sending verification email to: {}", email);

        // Use template service to get verification email template
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("verificationLink", frontendUrl + "/verify-email?token=" + token + "&userId=" + userId);

        String emailContent = templateService.getEmailTemplate("email-verification", templateData);

        // Send email using EmailService
        return emailService.sendEmail(
                email,
                "Verify Your Email - Foodie App",
                emailContent,
                true
        );
    }
}
