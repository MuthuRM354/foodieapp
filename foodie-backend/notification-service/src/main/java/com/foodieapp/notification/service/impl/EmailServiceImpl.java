package com.foodieapp.notification.service.impl;

import com.foodieapp.notification.dto.NotificationResponseDTO;
import com.foodieapp.notification.entity.NotificationLog;
import com.foodieapp.notification.exception.NotificationException;
import com.foodieapp.notification.repository.NotificationLogRepository;
import com.foodieapp.notification.service.EmailService;
import com.foodieapp.notification.service.EmailTemplateService;
import com.foodieapp.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final NotificationLogRepository notificationLogRepository;
    private final EmailTemplateService templateService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender,
                           NotificationLogRepository notificationLogRepository,
                           EmailTemplateService templateService) {
        this.mailSender = mailSender;
        this.notificationLogRepository = notificationLogRepository;
        this.templateService = templateService;
    }

    @Override
    public NotificationResponseDTO sendEmail(String to, String subject, String message, boolean isHtml) {
        // Log the attempt
        NotificationLog log = NotificationLog.builder()
                .type(NotificationType.EMAIL)
                .recipient(to)
                .subject(subject)
                .content(message)
                .sentAt(LocalDateTime.now())
                .build();

        if (!emailEnabled) {
            // Email service is disabled, log the message instead
            logger.info("EMAIL DISABLED - Would send to {}: Subject: {}", to, subject);
            log.setStatus("SIMULATED");
            log.setStatusMessage("Email service disabled, message logged only");
            notificationLogRepository.save(log);

            return NotificationResponseDTO.builder()
                    .success(true)
                    .message("Email simulation successful (service disabled)")
                    .recipient(to)
                    .status("SIMULATED")
                    .build();
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, isHtml);

            mailSender.send(mimeMessage);

            log.setStatus("SENT");
            log.setDeliveredAt(LocalDateTime.now());
            notificationLogRepository.save(log);

            return NotificationResponseDTO.builder()
                    .success(true)
                    .message("Email sent successfully")
                    .recipient(to)
                    .status("SENT")
                    .build();

        } catch (MessagingException e) {
            logger.error("Failed to send email: {}", e.getMessage());

            log.setStatus("FAILED");
            log.setErrorMessage("Error sending email: " + e.getMessage());
            notificationLogRepository.save(log);

            throw new NotificationException("Failed to send email: " + e.getMessage(), e);
        }
    }

    @Override
    public NotificationResponseDTO sendEmail(Map<String, Object> emailData) {
        String to = (String) emailData.get("to");
        String subject = (String) emailData.get("subject");
        String template = (String) emailData.get("template");

        // Default to true if not specified
        boolean isHtml = true;
        if (emailData.containsKey("isHtml")) {
            isHtml = Boolean.TRUE.equals(emailData.get("isHtml"));
        }

        // If a template is specified, use it
        String message;
        if (template != null && !template.isEmpty()) {
            message = templateService.getEmailTemplate(template, emailData);
        } else {
            message = (String) emailData.get("message");
        }

        return sendEmail(to, subject, message, isHtml);
    }

    @Override
    public NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp) {
        logger.info("Sending password reset email with OTP to: {}", email);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("otp", otp);
        templateData.put("resetLink", frontendUrl + "/reset-password?token=" + token);
        templateData.put("message", "You requested to reset your password. Use the following OTP to verify your identity:");

        String message = templateService.getEmailTemplate("otp-email", templateData);

        return sendEmail(email, "Reset Your Password - Foodie App", message, true);
    }

    @Override
    public NotificationResponseDTO sendEmailOtp(String email, String otp, String subject, String message) {
        logger.info("Sending OTP email to: {}", email);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("otp", otp);
        templateData.put("message", message);

        String emailContent = templateService.getEmailTemplate("otp-email", templateData);

        return sendEmail(email, subject, emailContent, true);
    }

    @Override
    public NotificationResponseDTO sendWelcomeEmail(String email, String name) {
        logger.info("Sending welcome email to: {}", email);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("frontendUrl", frontendUrl);

        String emailContent = templateService.getEmailTemplate("welcome-email", templateData);

        return sendEmail(email, "Welcome to Foodie App!", emailContent, true);
    }
}
