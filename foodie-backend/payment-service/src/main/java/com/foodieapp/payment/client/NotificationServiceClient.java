package com.foodieapp.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationServiceClient extends ServiceClient {

    public NotificationServiceClient(
            RestTemplate restTemplate,
            @Value("${services.notification.url:${notification.service.url}}") String notificationServiceUrl) {
        super(restTemplate, notificationServiceUrl);
    }

    /**
     * Send payment success notification
     */
    public void sendPaymentSuccessEmail(String email, String orderId, String amount, String paymentId) {
        if (email == null || email.isEmpty() || orderId == null || orderId.isEmpty()) {
            logger.warn("Missing required parameters for payment success email");
            return;
        }

        try {
            String path = "/api/v1/notifications/email";
            logger.info("Sending payment success email to {}", email);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", email);
            requestBody.put("subject", "Payment Successful for Order #" + orderId);
            requestBody.put("message", createPaymentSuccessMessage(orderId, amount, paymentId));

            postForObject(path, requestBody, Object.class, null);
            logger.info("Payment success notification sent to: {}", email);
        } catch (Exception e) {
            handleApiCallException("sending payment success email", e, null);
        }
    }

    /**
     * Send payment failure notification
     */
    public void sendPaymentFailureEmail(String email, String orderId, String amount, String reason) {
        if (email == null || email.isEmpty() || orderId == null || orderId.isEmpty()) {
            logger.warn("Missing required parameters for payment failure email");
            return;
        }

        try {
            String path = "/api/v1/notifications/email";
            logger.info("Sending payment failure email to {}", email);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", email);
            requestBody.put("subject", "Payment Failed for Order #" + orderId);
            requestBody.put("message", createPaymentFailureMessage(orderId, amount, reason));

            postForObject(path, requestBody, Object.class, null);
            logger.info("Payment failure notification sent to: {}", email);
        } catch (Exception e) {
            handleApiCallException("sending payment failure email", e, null);
        }
    }

    /**
     * Send payment status SMS
     */
    public void sendPaymentStatusSms(String phoneNumber, String orderId, boolean success, String amount) {
        if (phoneNumber == null || phoneNumber.isEmpty() || orderId == null || orderId.isEmpty()) {
            logger.warn("Missing required parameters for payment SMS");
            return;
        }

        try {
            String path = "/api/v1/notifications/sms";
            logger.info("Sending payment status SMS to {}", phoneNumber);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phoneNumber);

            String message;
            if (success) {
                message = "Foodie App: Payment of " + amount + " for order #" + orderId + " successful. Thank you!";
            } else {
                message = "Foodie App: Payment for order #" + orderId + " failed. Please check your payment details.";
            }
            requestBody.put("message", message);

            postForObject(path, requestBody, Object.class, null);
            logger.info("Payment status SMS notification sent to: {}", phoneNumber);
        } catch (Exception e) {
            handleApiCallException("sending payment status SMS", e, null);
        }
    }

    /**
     * Send refund notification
     */
    public void sendRefundNotification(String email, String orderId, String amount) {
        if (email == null || email.isEmpty() || orderId == null || orderId.isEmpty()) {
            logger.warn("Missing required parameters for refund notification");
            return;
        }

        try {
            String path = "/api/v1/notifications/email";
            logger.info("Sending refund notification to {}", email);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", email);
            requestBody.put("subject", "Refund Initiated for Order #" + orderId);
            requestBody.put("message", "Your refund of " + amount + " for order #" + orderId +
                    " has been initiated. It may take 5-7 business days to reflect in your account.");

            postForObject(path, requestBody, Object.class, null);
            logger.info("Refund notification sent to: {}", email);
        } catch (Exception e) {
            handleApiCallException("sending refund notification", e, null);
        }
    }

    // Helper methods for message formatting
    private String createPaymentSuccessMessage(String orderId, String amount, String paymentId) {
        StringBuilder message = new StringBuilder();
        message.append("Your payment of ").append(amount)
                .append(" for order #").append(orderId)
                .append(" has been successfully processed.");

        if (paymentId != null && !paymentId.isEmpty()) {
            message.append("\n\nPayment Reference: ").append(paymentId);
        }

        message.append("\n\nThank you for your order!");
        return message.toString();
    }

    private String createPaymentFailureMessage(String orderId, String amount, String reason) {
        StringBuilder message = new StringBuilder();
        message.append("We were unable to process your payment of ").append(amount)
                .append(" for order #").append(orderId);

        if (reason != null && !reason.isEmpty()) {
            message.append(".\n\nReason: ").append(reason);
        }

        message.append("\n\nPlease update your payment information or try again later.");
        return message.toString();
    }
}

