package com.foodieapp.order.service;

import com.foodieapp.order.client.NotificationServiceClient;
import com.foodieapp.order.client.RestaurantServiceClient;
import com.foodieapp.order.client.UserServiceClient;
import com.foodieapp.order.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExternalServiceClientImpl implements ExternalServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(ExternalServiceClientImpl.class);

    private final UserServiceClient userServiceClient;
    private final RestaurantServiceClient restaurantServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public ExternalServiceClientImpl(
            UserServiceClient userServiceClient,
            RestaurantServiceClient restaurantServiceClient,
            NotificationServiceClient notificationServiceClient) {
        this.userServiceClient = userServiceClient;
        this.restaurantServiceClient = restaurantServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Override
    public Map<String, Object> validateToken(String authToken) {
        return userServiceClient.validateToken(authToken);
    }

    @Override
    public Map<String, Object> getUserById(String userId, String authToken) {
        return userServiceClient.getUserDetails(userId, authToken);
    }

    @Override
    public Map<String, Object> getRestaurantById(String restaurantId, String authToken) {
        return restaurantServiceClient.getRestaurantInfo(restaurantId, authToken);
    }

    @Override
    public String extractUserIdFromToken(String authToken) {
        return userServiceClient.extractUserIdFromToken(authToken);
    }

    @Override
    public boolean validateUser(String userId, String authToken) {
        Map<String, Object> tokenInfo = validateToken(authToken);
        if (Boolean.TRUE.equals(tokenInfo.get("valid"))) {
            String tokenUserId = tokenInfo.get("userId").toString();
            return userId.equals(tokenUserId);
        }
        return false;
    }

    @Override
    public boolean verifyRestaurant(String restaurantId, String authToken) {
        return restaurantServiceClient.verifyRestaurantExists(restaurantId, authToken);
    }

    @Override
    public Map<String, Object> getMenuItemDetails(String restaurantId, String itemId, String authToken) {
        return restaurantServiceClient.getMenuItemDetails(restaurantId, itemId, authToken);
    }

    @Override
    public void notifyOrderStatusChange(Order order, String oldStatus) {
        try {
            // Get user details
            Map<String, Object> userDetails = getUserById(order.getUserId(), null);

            if (userDetails != null && Boolean.TRUE.equals(userDetails.get("exists"))) {
                String email = (String) userDetails.get("email");
                String phoneNumber = (String) userDetails.get("phoneNumber");

                // Send appropriate notifications
                if (email != null && !email.isEmpty()) {
                    notificationServiceClient.sendOrderStatusEmail(
                            email,
                            order.getId(),
                            order.getStatus().toString(),
                            order.getRestaurantName());
                }

                // Only send SMS for important status changes
                if (phoneNumber != null && !phoneNumber.isEmpty() &&
                    isImportantStatusChange(order.getStatus().toString())) {
                    notificationServiceClient.sendOrderStatusSms(
                            phoneNumber,
                            order.getId(),
                            order.getStatus().toString(),
                            order.getRestaurantName());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to send order status notification: {}", e.getMessage());
        }
    }

    private boolean isImportantStatusChange(String status) {
        return "CONFIRMED".equals(status) ||
               "OUT_FOR_DELIVERY".equals(status) ||
               "DELIVERED".equals(status) ||
               "CANCELLED".equals(status);
    }
}
