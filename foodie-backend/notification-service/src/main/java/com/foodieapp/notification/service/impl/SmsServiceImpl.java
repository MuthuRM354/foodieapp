package com.foodieapp.notification.service.impl;

import com.foodieapp.notification.dto.SmsNotificationDTO;
import com.foodieapp.notification.entity.NotificationLog;
import com.foodieapp.notification.repository.NotificationLogRepository;
import com.foodieapp.notification.service.SmsService;
import com.foodieapp.notification.util.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SmsServiceImpl implements SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    private final RestTemplate restTemplate;
    private final NotificationLogRepository notificationLogRepository;

    @Value("${app.sms.api.url}")
    private String smsApiUrl;

    @Value("${app.sms.api.key}")
    private String smsApiKey;

    @Value("${app.sms.enabled:false}")
    private boolean smsEnabled;

    @Autowired
    public SmsServiceImpl(RestTemplate restTemplate, NotificationLogRepository notificationLogRepository) {
        this.restTemplate = restTemplate;
        this.notificationLogRepository = notificationLogRepository;
    }

    @Override
    public boolean sendSms(String to, String message) {
        // Log the SMS attempt
        NotificationLog log = new NotificationLog();
        log.setRecipient(to);
        log.setType(NotificationType.SMS);
        log.setContent(message);
        log.setSentAt(LocalDateTime.now());

        if (!smsEnabled) {
            // SMS service is disabled, log the message instead
            logger.info("SMS DISABLED - Would send to {}: {}", to, message);
            log.setStatus("SIMULATED");
            log.setStatusMessage("SMS service disabled, message logged only");
            notificationLogRepository.save(log);
            return true; // Return success as this is expected behavior when disabled
        }

        try {
            // Replace with your actual SMS provider implementation
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("phone", to);
            requestBody.put("message", message);
            requestBody.put("key", smsApiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call to SMS provider
            Map<String, Object> response = restTemplate.postForObject(smsApiUrl, request, Map.class);

            boolean success = response != null && Boolean.TRUE.equals(response.get("success"));

            // Record result
            log.setStatus(success ? "SENT" : "FAILED");
            log.setStatusMessage(response != null ? response.toString() : "No response from SMS provider");
            notificationLogRepository.save(log);

            return success;
        } catch (Exception e) {
            logger.error("Failed to send SMS: {}", e.getMessage());

            // Record the failure
            log.setStatus("ERROR");
            log.setStatusMessage("Error sending SMS: " + e.getMessage());
            notificationLogRepository.save(log);

            return false;
        }
    }

    @Override
    public boolean sendSms(SmsNotificationDTO notification) {
        // Override default method to log the original notification
        logger.debug("Received SMS notification: {}", notification.getTo());
        return sendSms(notification.getTo(), notification.getMessage());
    }
}
