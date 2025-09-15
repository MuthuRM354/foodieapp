package com.foodieapp.notification.controller;

import com.foodieapp.notification.client.UserServiceClient;
import com.foodieapp.notification.dto.EmailNotificationDTO;
import com.foodieapp.notification.dto.EmailRequest;
import com.foodieapp.notification.dto.NotificationResponseDTO;
import com.foodieapp.notification.dto.SmsNotificationDTO;
import com.foodieapp.notification.entity.NotificationLog;
import com.foodieapp.notification.exception.NotificationException;
import com.foodieapp.notification.repository.NotificationLogRepository;
import com.foodieapp.notification.service.NotificationService;
import com.foodieapp.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final NotificationLogRepository notificationLogRepository;
    private final UserServiceClient userServiceClient;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  NotificationLogRepository notificationLogRepository,
                                  UserServiceClient userServiceClient) {
        this.notificationService = notificationService;
        this.notificationLogRepository = notificationLogRepository;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/email")
    public ResponseEntity<NotificationResponseDTO> sendEmailNotification(
            @Valid @RequestBody EmailNotificationDTO emailNotification) {
        logger.info("Received request to send email to: {}", emailNotification.getTo());
        NotificationResponseDTO response = notificationService.sendEmailNotification(emailNotification);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/password-reset")
    public ResponseEntity<NotificationResponseDTO> sendPasswordResetEmail(
            @RequestBody EmailRequest request) {
        // Centralized validation
        validateEmailRequest(request, true, false, true);
        String email = resolveEmail(request);
        String token = request.getToken();
        String otp = request.getOtp();

        logger.info("Received request to send password reset email to: {}", email);
        NotificationResponseDTO response = notificationService.sendPasswordResetEmail(email, token, otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/otp")
    public ResponseEntity<NotificationResponseDTO> sendOtpEmail(
            @RequestBody EmailRequest request) {
        // Centralized validation
        validateEmailRequest(request, true, false, false);
        String email = resolveEmail(request);
        String otp = request.getOtp();
        String subject = request.getSubject();
        String message = request.getMessage();

        logger.info("Received request to send OTP email to: {}", email);
        NotificationResponseDTO response = notificationService.sendOtpEmail(email, otp, subject, message);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/welcome")
    public ResponseEntity<NotificationResponseDTO> sendWelcomeEmail(
            @RequestBody EmailRequest request) {
        // Centralized validation
        validateEmailRequest(request, false, false, false);
        String email = resolveEmail(request);
        String name = request.getMessage(); // Using message field to pass the name

        logger.info("Received request to send welcome email to: {}", email);
        NotificationResponseDTO response = notificationService.sendWelcomeEmail(email, name);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email/verification")
    public ResponseEntity<NotificationResponseDTO> sendVerificationEmail(
            @RequestBody EmailRequest request) {
        // Centralized validation
        validateEmailRequest(request, false, true, false);
        String email = resolveEmail(request);
        String token = request.getToken();
        String userId = request.getUserId();

        logger.info("Received request to send verification email to: {}", email);
        NotificationResponseDTO response = notificationService.sendVerificationEmail(email, token, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sms")
    public ResponseEntity<NotificationResponseDTO> sendSmsNotification(
            @Valid @RequestBody SmsNotificationDTO smsNotification) {
        logger.info("Received request to send SMS to: {}", smsNotification.getTo());
        NotificationResponseDTO response = notificationService.sendSmsNotification(smsNotification);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<NotificationLog>> getNotificationLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) NotificationType type,
            @RequestHeader(value = "Authorization", required = false) String authToken) {

        // Verify admin permission
        validateAdminAccess(authToken);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));

        Page<NotificationLog> logs;
        if (recipient != null && type != null) {
            logs = notificationLogRepository.findByRecipientAndType(recipient, type, pageable);
        } else if (recipient != null) {
            logs = notificationLogRepository.findByRecipient(recipient, pageable);
        } else if (type != null) {
            logs = notificationLogRepository.findByType(type, pageable);
        } else {
            logs = notificationLogRepository.findAll(pageable);
        }

        return ResponseEntity.ok(logs);
    }

    // Helper method to centralize email field resolution
    private String resolveEmail(EmailRequest request) {
        return request.getTo(); // The getter now handles the fallback to email field
    }

    // Helper method to centralize validation for email requests
    private void validateEmailRequest(EmailRequest request, boolean requireOtp, boolean requireUserId, boolean requireToken) {
        if (request.getTo() == null && request.getEmail() == null) {
            throw new NotificationException("Email address is required");
        }

        if (requireOtp && request.getOtp() == null) {
            throw new NotificationException("OTP is required");
        }

        if (requireUserId && request.getUserId() == null) {
            throw new NotificationException("User ID is required");
        }

        if (requireToken && request.getToken() == null) {
            throw new NotificationException("Token is required");
        }
    }

    // Helper method to validate admin access
    private void validateAdminAccess(String authToken) {
        if (authToken == null || authToken.isEmpty()) {
            throw new SecurityException("Authentication required");
        }

        Map<String, Object> tokenInfo = userServiceClient.validateToken(authToken);

        if (!Boolean.TRUE.equals(tokenInfo.get("valid"))) {
            throw new SecurityException("Invalid authentication token");
        }

        // Check if user has admin role
        if (tokenInfo.get("roles") instanceof Iterable) {
            for (Object role : (Iterable<?>) tokenInfo.get("roles")) {
                if ("ROLE_ADMIN".equals(role)) {
                    return; // Admin access granted
                }
            }
        }

        throw new SecurityException("Admin access required");
    }
}

