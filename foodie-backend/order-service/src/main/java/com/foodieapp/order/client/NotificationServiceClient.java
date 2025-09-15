package com.foodieapp.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationServiceClient extends BaseServiceClient {

    public NotificationServiceClient(
            RestTemplate restTemplate,
            @Value("${notification.service.url}") String notificationServiceUrl) {
        super(restTemplate, notificationServiceUrl);
    }

    /**
     * Send order status notification email
     */
    public void sendOrderStatusEmail(String email, String orderId, String status, String restaurantName) {
        try {
            String url = "/api/v1/notifications/email";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", email);
            requestBody.put("subject", "Order #" + orderId + " " + formatStatus(status));
            requestBody.put("message", createOrderStatusMessage(orderId, status, restaurantName, false));

            postForObject(url, requestBody, Object.class, "");
            logger.info("Order status email notification sent to: {}", email);
        } catch (Exception e) {
            handleApiCallException("sending order status email", e, null);
        }
    }

    /**
     * Send order status notification via SMS
     */
    public void sendOrderStatusSms(String phoneNumber, String orderId, String status, String restaurantName) {
        try {
            String url = "/api/v1/notifications/sms";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phoneNumber);
            requestBody.put("message", createOrderStatusMessage(orderId, status, restaurantName, true));

            postForObject(url, requestBody, Object.class, "");
            logger.info("Order status SMS notification sent to: {}", phoneNumber);
        } catch (Exception e) {
            handleApiCallException("sending order status SMS", e, null);
        }
    }

    /**
     * Send notification about new order to restaurant
     */
    public void notifyRestaurantAboutNewOrder(String email, String orderId, String customerName) {
        try {
            String url = "/api/v1/notifications/email";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", email);
            requestBody.put("subject", "New Order #" + orderId + " Received");
            requestBody.put("message", "You have received a new order #" + orderId +
                    " from " + customerName + ". Please check your dashboard to confirm.");

            postForObject(url, requestBody, Object.class, "");
            logger.info("New order notification sent to restaurant: {}", email);
        } catch (Exception e) {
            handleApiCallException("notifying restaurant about new order", e, null);
        }
    }

    // Helper methods for message formatting
    private String formatStatus(String status) {
        switch (status) {
            case "CONFIRMED": return "Confirmed";
            case "PREPARING": return "Being Prepared";
            case "READY": return "Ready for Pickup";
            case "OUT_FOR_DELIVERY": return "Out for Delivery";
            case "DELIVERED": return "Delivered";
            case "CANCELLED": return "Cancelled";
            default: return status;
        }
    }

    /**
     * Unified message creation for both email and SMS
     * @param orderId Order ID
     * @param status Order status
     * @param restaurantName Restaurant name
     * @param isShortFormat true for SMS (shorter message), false for email (longer message)
     * @return Formatted message
     */
    private String createOrderStatusMessage(String orderId, String status, String restaurantName, boolean isShortFormat) {
        StringBuilder message = new StringBuilder();

        if (isShortFormat) {
            message.append("Foodie App: Order #").append(orderId);
        } else {
            message.append("Your order #").append(orderId);
            if (restaurantName != null && !restaurantName.isEmpty()) {
                message.append(" from ").append(restaurantName);
            }
        }

        switch (status) {
            case "CONFIRMED":
                message.append(isShortFormat ? " confirmed & being prepared." :
                    " has been confirmed. The restaurant is preparing your food.");
                break;
            case "PREPARING":
                message.append(isShortFormat ? " is being prepared." :
                    " is being prepared. We'll notify you once it's ready.");
                break;
            case "READY":
                message.append(isShortFormat ? " is ready for pickup." :
                    " is ready for pickup.");
                break;
            case "OUT_FOR_DELIVERY":
                message.append(isShortFormat ? " out for delivery." :
                    " is on its way to you. Delivery expected soon.");
                break;
            case "DELIVERED":
                message.append(isShortFormat ? " delivered. Enjoy!" :
                    " has been delivered. Enjoy your meal!");
                break;
            case "CANCELLED":
                message.append(isShortFormat ? " cancelled." :
                    " has been cancelled. Please contact support if you have any questions.");
                break;
            default:
                message.append(isShortFormat ? " status: " + status :
                    " status has been updated to: " + status);
        }

        return message.toString();
    }
}
