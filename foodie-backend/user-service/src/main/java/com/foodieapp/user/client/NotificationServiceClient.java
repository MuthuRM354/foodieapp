package com.foodieapp.user.client;

import com.foodieapp.user.dto.response.NotificationResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);
    private final JavaMailSender mailSender; // For fallback

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public NotificationServiceClient(
            RestTemplate restTemplate,
            @Value("${notification.service.url}") String notificationServiceUrl,
            JavaMailSender mailSender) {
        super(restTemplate, notificationServiceUrl);
        this.mailSender = mailSender;
    }

    /**
     * Send OTP email notification
     */
    public NotificationResponseDTO sendOtpEmail(String email, String otp, String subject, String message) {
        try {
            String path = "/api/v1/notifications/email/otp";

            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("otp", otp);
            request.put("subject", subject);
            request.put("message", message);

            return postForObject(path, request, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Error sending OTP email: {}", e.getMessage());
            NotificationResponseDTO response = new NotificationResponseDTO();
            response.setSuccess(false);
            response.setMessage("Failed to send OTP email: " + e.getMessage());
            return response;
        }
    }

    /**
     * Send password reset email
     */
    public NotificationResponseDTO sendPasswordResetEmail(String email, String token, String otp) {
        try {
            String path = "/api/v1/notifications/email/password-reset";

            Map<String, String> request = new HashMap<>();
            request.put("email", email);
            request.put("token", token);
            request.put("otp", otp);

            return postForObject(path, request, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Error sending password reset email: {}", e.getMessage());

            // Fallback: send directly if notification service fails
            try {
                String resetLink = frontendUrl + "/reset-password?token=" + token;
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(senderEmail);
                message.setTo(email);
                message.setSubject("Password Reset Request");
                message.setText("You've requested to reset your password.\n\n"
                        + "Your OTP code is: " + otp + "\n\n"
                        + "Or click this link to reset your password: " + resetLink + "\n\n"
                        + "If you didn't request this, please ignore this email.\n"
                        + "This link and OTP will expire in 30 minutes.");

                mailSender.send(message);

                NotificationResponseDTO response = new NotificationResponseDTO();
                response.setSuccess(true);
                response.setMessage("Password reset email sent directly (fallback)");
                response.setRecipient(email);
                response.setStatus("SENT_FALLBACK");
                return response;
            } catch (Exception fallbackEx) {
                logger.error("Fallback also failed: {}", fallbackEx.getMessage());
                NotificationResponseDTO response = new NotificationResponseDTO();
                response.setSuccess(false);
                response.setMessage("Failed to send password reset email: " + e.getMessage() + ". Fallback also failed.");
                return response;
            }
        }
    }

    /**
     * Send SMS notification
     */
    public NotificationResponseDTO sendSmsOtp(String phoneNumber, String otp) {
        try {
            String path = "/api/v1/notifications/sms";

            Map<String, String> request = new HashMap<>();
            request.put("to", phoneNumber);
            request.put("message", "Your Foodie App verification code is: " + otp);

            return postForObject(path, request, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Error sending SMS OTP: {}", e.getMessage());
            NotificationResponseDTO response = new NotificationResponseDTO();
            response.setSuccess(false);
            response.setMessage("Failed to send SMS OTP: " + e.getMessage());
            return response;
        }
    }

    /**
     * Send general email
     */
    public NotificationResponseDTO sendEmail(Map<String, Object> emailData) {
        try {
            String path = "/api/v1/notifications/email";
            return postForObject(path, emailData, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage());
            NotificationResponseDTO response = new NotificationResponseDTO();
            response.setSuccess(false);
            response.setMessage("Failed to send email: " + e.getMessage());
            return response;
        }
    }
}
