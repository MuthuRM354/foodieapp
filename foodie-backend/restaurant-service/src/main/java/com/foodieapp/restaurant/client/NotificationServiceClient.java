package com.foodieapp.restaurant.client;

import com.foodieapp.restaurant.dto.notification.EmailNotificationDTO;
import com.foodieapp.restaurant.dto.notification.SmsNotificationDTO;
import com.foodieapp.restaurant.dto.notification.NotificationResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceClient.class);

    public NotificationServiceClient(
            RestTemplate restTemplate,
            @Value("${notification.service.url}") String notificationServiceUrl) {
        super(restTemplate, notificationServiceUrl);
    }

    /**
     * Send email notification
     */
    public NotificationResponseDTO sendEmailNotification(EmailNotificationDTO emailNotification) {
        try {
            String path = "/api/v1/notifications/email";
            return postForObject(path, emailNotification, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Failed to send email notification: {}", e.getMessage());
            // Create and return a failure response
            NotificationResponseDTO failureResponse = new NotificationResponseDTO();
            failureResponse.setSuccess(false);
            failureResponse.setMessage("Failed to send email: " + e.getMessage());
            return failureResponse;
        }
    }

    /**
     * Send SMS notification
     */
    public NotificationResponseDTO sendSmsNotification(SmsNotificationDTO smsNotification) {
        try {
            String path = "/api/v1/notifications/sms";
            return postForObject(path, smsNotification, NotificationResponseDTO.class, "");
        } catch (Exception e) {
            logger.error("Failed to send SMS notification: {}", e.getMessage());
            // Create and return a failure response
            NotificationResponseDTO failureResponse = new NotificationResponseDTO();
            failureResponse.setSuccess(false);
            failureResponse.setMessage("Failed to send SMS: " + e.getMessage());
            return failureResponse;
        }
    }

    /**
     * Send restaurant approval notification
     */
    public NotificationResponseDTO sendRestaurantApprovalNotification(String email, String restaurantName) {
        EmailNotificationDTO notification = new EmailNotificationDTO();
        notification.setTo(email);
        notification.setSubject("Restaurant Approved: " + restaurantName);
        notification.setMessage("Congratulations! Your restaurant '" + restaurantName +
                "' has been approved and is now visible to customers on Foodie App.");

        return sendEmailNotification(notification);
    }

    /**
     * Send restaurant rejection notification
     */
    public NotificationResponseDTO sendRestaurantRejectionNotification(String email, String restaurantName, String reason) {
        EmailNotificationDTO notification = new EmailNotificationDTO();
        notification.setTo(email);
        notification.setSubject("Restaurant Update: " + restaurantName);
        notification.setMessage("We're sorry to inform you that your restaurant '" + restaurantName +
                "' could not be approved at this time. Reason: " + reason +
                "\n\nPlease make the necessary updates and resubmit.");

        return sendEmailNotification(notification);
    }
}
