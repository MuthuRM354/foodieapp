package com.foodieapp.restaurant.service.notification;

import com.foodieapp.restaurant.client.NotificationServiceClient;
import com.foodieapp.restaurant.dto.notification.EmailNotificationDTO;
import com.foodieapp.restaurant.dto.notification.NotificationResponseDTO;
import com.foodieapp.restaurant.dto.notification.SmsNotificationDTO;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public NotificationService(NotificationServiceClient notificationServiceClient) {
        this.notificationServiceClient = notificationServiceClient;
    }

    /**
     * Standardized method for restaurant status notifications
     *
     * @param restaurant The restaurant to notify about
     * @param status The status change ("verified", "approved", "rejected")
     * @param reason Optional reason for rejection
     * @return Notification response
     */
    public NotificationResponseDTO sendRestaurantStatusNotification(Restaurant restaurant, String status, String reason) {
        try {
            String email = restaurant.getEmail();
            String restaurantName = restaurant.getName();

            switch (status.toLowerCase()) {
                case "verified":
                case "approved":
                    return sendRestaurantApprovalEmail(email, restaurantName);
                case "rejected":
                    return sendRestaurantRejectionEmail(email, restaurantName, reason);
                default:
                    throw new IllegalArgumentException("Unknown status: " + status);
            }
        } catch (Exception e) {
            logger.error("Failed to send restaurant status notification: {}", e.getMessage(), e);
            NotificationResponseDTO failureResponse = new NotificationResponseDTO();
            failureResponse.setSuccess(false);
            failureResponse.setMessage("Failed to send status notification: " + e.getMessage());
            return failureResponse;
        }
    }

    /**
     * Send restaurant verification notification to owner
     * Legacy method - delegates to standardized method
     */
    public NotificationResponseDTO sendRestaurantVerificationNotification(Restaurant restaurant) {
        return sendRestaurantStatusNotification(restaurant, "verified", null);
    }

    /**
     * Send restaurant approval notification
     * Legacy method - delegates to standardized method
     */
    public NotificationResponseDTO sendRestaurantApprovalNotification(String email, String restaurantName) {
        Restaurant dummyRestaurant = new Restaurant();
        dummyRestaurant.setEmail(email);
        dummyRestaurant.setName(restaurantName);
        return sendRestaurantStatusNotification(dummyRestaurant, "approved", null);
    }

    /**
     * Send restaurant rejection notification
     * Legacy method - delegates to standardized method
     */
    public NotificationResponseDTO sendRestaurantRejectionNotification(String email, String restaurantName, String reason) {
        Restaurant dummyRestaurant = new Restaurant();
        dummyRestaurant.setEmail(email);
        dummyRestaurant.setName(restaurantName);
        return sendRestaurantStatusNotification(dummyRestaurant, "rejected", reason);
    }

    // Private helper methods for creating specific notifications

    private NotificationResponseDTO sendRestaurantApprovalEmail(String email, String restaurantName) {
        EmailNotificationDTO notification = new EmailNotificationDTO();
        notification.setTo(email);
        notification.setSubject("Restaurant Approved: " + restaurantName);
        notification.setMessage("Congratulations! Your restaurant '" + restaurantName +
                "' has been approved and is now visible to customers on Foodie App.");
        notification.setHtml(true);

        return notificationServiceClient.sendEmailNotification(notification);
    }

    private NotificationResponseDTO sendRestaurantRejectionEmail(String email, String restaurantName, String reason) {
        EmailNotificationDTO notification = new EmailNotificationDTO();
        notification.setTo(email);
        notification.setSubject("Restaurant Update: " + restaurantName);
        notification.setMessage("We're sorry to inform you that your restaurant '" + restaurantName +
                "' could not be approved at this time. Reason: " + reason +
                "\n\nPlease make the necessary updates and resubmit.");
        notification.setHtml(true);

        return notificationServiceClient.sendEmailNotification(notification);
    }

    /**
     * Send menu update notification to customers (for marketing)
     */
    public NotificationResponseDTO sendMenuUpdateNotification(Restaurant restaurant, String customerEmail) {
        try {
            // Create email content
            String subject = "New Menu Items at " + restaurant.getName();
            String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
                    + "<h2 style='color: #FF5722;'>New Menu Items Available!</h2>"
                    + "<p>Hello Foodie,</p>"
                    + "<p>One of your favorite restaurants, <strong>" + restaurant.getName() + "</strong>, "
                    + "has updated their menu with exciting new dishes!</p>"
                    + "<p>Check them out now on our app or website.</p>"
                    + "<p>Happy eating!</p>"
                    + "<p>The Foodie App Team</p>"
                    + "</div>";

            // Create email notification DTO
            EmailNotificationDTO emailNotification = new EmailNotificationDTO(
                    customerEmail,
                    subject,
                    body,
                    true // isHtml
            );

            // Send notification
            NotificationResponseDTO response = notificationServiceClient.sendEmailNotification(emailNotification);
            logger.info("Menu update notification sent to: {}", customerEmail);
            return response;
        } catch (Exception e) {
            logger.error("Failed to send menu update notification: {}", e.getMessage(), e);
            NotificationResponseDTO failureResponse = new NotificationResponseDTO();
            failureResponse.setSuccess(false);
            failureResponse.setMessage("Failed to send menu update notification: " + e.getMessage());
            return failureResponse;
        }
    }

    /**
     * Send menu item update notification to restaurant owner
     */
    public NotificationResponseDTO sendMenuItemUpdateNotification(Restaurant restaurant, MenuItem menuItem) {
        try {
            EmailNotificationDTO notification = new EmailNotificationDTO();
            notification.setTo(restaurant.getEmail());
            notification.setSubject("Menu Item Updated: " + menuItem.getName());
            notification.setMessage("Your menu item '" + menuItem.getName() +
                    "' for restaurant '" + restaurant.getName() +
                    "' has been updated successfully.");

            return notificationServiceClient.sendEmailNotification(notification);
        } catch (Exception e) {
            logger.error("Failed to send menu item update notification: {}", e.getMessage());
            NotificationResponseDTO failureResponse = new NotificationResponseDTO();
            failureResponse.setSuccess(false);
            failureResponse.setMessage("Failed to send notification: " + e.getMessage());
            return failureResponse;
        }
    }

    /**
     * Send SMS notification
     */
    public NotificationResponseDTO sendSmsNotification(SmsNotificationDTO smsNotification) {
        return notificationServiceClient.sendSmsNotification(smsNotification);
    }

    /**
     * Send email notification
     */
    public NotificationResponseDTO sendEmailNotification(EmailNotificationDTO emailNotification) {
        return notificationServiceClient.sendEmailNotification(emailNotification);
    }
}
