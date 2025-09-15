package com.foodieapp.user.controller;

import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.service.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Provides user-service specific email functionality.
 * This controller serves as a facade to the underlying notification service
 * for simple email sending requirements within the user service.
 */
@RestController
@RequestMapping("/api/v1/email")
public class EmailController {
    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends a general email using the notification service.
     * This endpoint provides a simplified interface for sending emails within the user service.
     * For more specialized notification needs, the notification service should be used directly.
     *
     * @param emailRequest Map containing email parameters:
     *                    - to: recipient email address
     *                    - subject: email subject
     *                    - message: email content
     *                    - isHtml: whether the content is HTML (defaults to true)
     *                    - cc: optional list of CC recipients
     *                    - bcc: optional list of BCC recipients
     * @return Response indicating success or failure
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendEmail(@RequestBody Map<String, Object> emailRequest) {
        try {
            String to = (String) emailRequest.get("to");
            String subject = (String) emailRequest.get("subject");
            String message = (String) emailRequest.get("message");
            boolean isHtml = emailRequest.containsKey("isHtml") ? (boolean) emailRequest.get("isHtml") : true;

            List<String> cc = emailRequest.containsKey("cc") ? (List<String>) emailRequest.get("cc") : null;
            List<String> bcc = emailRequest.containsKey("bcc") ? (List<String>) emailRequest.get("bcc") : null;

            // Currently, we only use the standard email methods available in the service
            // If simple notification, use sendEmailOtp
            emailService.sendEmailOtp(to, "", subject, message);

            logger.info("Email sent to {} with subject: {}", to, subject);
            return ResponseEntity.ok(ApiResponse.success("Email sent successfully", null));
        } catch (Exception e) {
            logger.error("Failed to send email: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to send email: " + e.getMessage()));
        }
    }

 }
