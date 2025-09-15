package com.foodieapp.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailTemplateService {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final Map<String, String> templateCache = new HashMap<>();

    /**
     * Get an email template, with placeholders replaced by provided values
     */
    public String getEmailTemplate(String templateName, Map<String, Object> templateData) {
        String template = getTemplateContent(templateName);

        // Replace all placeholders in the template
        for (Map.Entry<String, Object> entry : templateData.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }

        return template;
    }

    /**
     * Get the raw template content
     */
    private String getTemplateContent(String templateName) {
        // Check if template is already cached
        if (templateCache.containsKey(templateName)) {
            return templateCache.get(templateName);
        }

        // Otherwise, load the appropriate template based on name
        String template;
        switch (templateName) {
            case "password-reset":
                template = getPasswordResetTemplate();
                break;
            case "email-verification":
                template = getEmailVerificationTemplate();
                break;
            case "otp-email":
                template = getOtpEmailTemplate();
                break;
            case "welcome-email":
                template = getWelcomeEmailTemplate();
                break;
            default:
                template = getDefaultTemplate();
        }

        // Cache the template
        templateCache.put(templateName, template);
        return template;
    }

    private String getPasswordResetTemplate() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Password Reset Request</h2>"
                + "<p>We received a request to reset your password. Click the button below to reset it:</p>"
                + "<p><a href='{{resetLink}}' style='padding: 10px 15px; background-color: #4CAF50; color: white; "
                + "text-decoration: none; border-radius: 4px;'>Reset Password</a></p>"
                + "<p>If the button doesn't work, copy and paste this link into your browser:</p>"
                + "<p>{{resetLink}}</p>"
                + "<p>This link will expire in 30 minutes.</p>"
                + "<p>If you didn't request a password reset, you can safely ignore this email.</p>"
                + "</div>"
                + "</body></html>";
    }

    private String getEmailVerificationTemplate() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Welcome to Foodie App!</h2>"
                + "<p>Thank you for signing up. Please verify your email address by clicking the link below:</p>"
                + "<p><a href='{{verificationLink}}' style='padding: 10px 15px; background-color: #4CAF50; color: white; "
                + "text-decoration: none; border-radius: 4px;'>Verify Email Address</a></p>"
                + "<p>If the button doesn't work, copy and paste this link into your browser:</p>"
                + "<p>{{verificationLink}}</p>"
                + "<p>This link will expire in 24 hours.</p>"
                + "<p>If you didn't create an account, you can safely ignore this email.</p>"
                + "</div>"
                + "</body></html>";
    }

    private String getOtpEmailTemplate() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Your One-Time Password</h2>"
                + "<p>{{message}}</p>"
                + "<div style='padding: 15px; background-color: #f5f5f5; text-align: center; font-size: 24px; font-weight: bold; letter-spacing: 5px;'>"
                + "{{otp}}"
                + "</div>"
                + "<p>This OTP will expire in 10 minutes.</p>"
                + "<p>If you didn't request this OTP, please ignore this email.</p>"
                + "</div>"
                + "</body></html>";
    }

    private String getWelcomeEmailTemplate() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>Welcome to Foodie App, {{name}}!</h2>"
                + "<p>Thank you for joining Foodie App. We're excited to have you on board!</p>"
                + "<p>Discover restaurants, order food, and enjoy delicious meals with just a few clicks.</p>"
                + "<p><a href='{{frontendUrl}}' style='padding: 10px 15px; background-color: #4CAF50; color: white; "
                + "text-decoration: none; border-radius: 4px;'>Start Exploring</a></p>"
                + "</div>"
                + "</body></html>";
    }

    private String getDefaultTemplate() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\">" +
                "<div style='font-family: Arial, sans-serif; max-width: 600px;'>"
                + "<h2>{{subject}}</h2>"
                + "<p>{{message}}</p>"
                + "</div>"
                + "</body></html>";
    }
}