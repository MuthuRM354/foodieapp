package com.foodieapp.order.service;

import com.foodieapp.order.model.Order;

import java.util.HashMap;
import java.util.Map;

public interface ExternalServiceClient {
    /**
     * Validate token with authentication service
     * @param authToken Authentication token
     * @return Map containing validation result and user info
     */
    Map<String, Object> validateToken(String authToken);

    /**
     * Get user by ID
     * @param userId User ID
     * @param authToken Authentication token
     * @return User details as a map
     */
    Map<String, Object> getUserById(String userId, String authToken);

    /**
     * Get restaurant by ID
     * @param restaurantId Restaurant ID
     * @param authToken Authentication token
     * @return Restaurant details as a map
     */
    Map<String, Object> getRestaurantById(String restaurantId, String authToken);

    /**
     * Extract user ID from token
     * @param authToken Authentication token
     * @return User ID from token
     */
    String extractUserIdFromToken(String authToken);

    /**
     * Validate user with authentication token
     * @param userId User ID to validate
     * @param authToken Authentication token
     * @return true if user is valid and authorized
     */
    default boolean validateUser(String userId, String authToken) {
        Map<String, Object> tokenInfo = validateToken(authToken);
        if (Boolean.TRUE.equals(tokenInfo.get("valid"))) {
            String tokenUserId = tokenInfo.get("userId").toString();
            return userId.equals(tokenUserId);
        }
        return false;
    }

    /**
     * Validate user (without token - for backward compatibility)
     * @param userId User ID to validate
     * @return true if user is valid
     */
    default boolean validateUser(String userId) {
        return userId != null && !userId.isEmpty();
    }

    /**
     * Get user details by ID
     * @param userId User ID
     * @param authToken Authentication token
     * @return User details as a map
     */
    default Map<String, Object> getUserDetails(String userId, String authToken) {
        return getUserById(userId, authToken);
    }

    /**
     * Get restaurant details by ID
     * @param restaurantId Restaurant ID
     * @param authToken Authentication token
     * @return Restaurant details as a map
     */
    default Map<String, Object> getRestaurantDetails(String restaurantId, String authToken) {
        return getRestaurantById(restaurantId, authToken);
    }

    /**
     * Verify if restaurant exists and is active
     * @param restaurantId Restaurant ID
     * @param authToken Authentication token
     * @return true if restaurant exists and is active
     */
    default boolean verifyRestaurant(String restaurantId, String authToken) {
        Map<String, Object> restaurant = getRestaurantById(restaurantId, authToken);
        return restaurant != null && !restaurant.isEmpty() &&
                Boolean.TRUE.equals(restaurant.getOrDefault("isActive", false));
    }

    /**
     * Validate restaurant (without token - for backward compatibility)
     * @param restaurantId Restaurant ID to validate
     * @return true if restaurant is valid
     */
    default boolean validateRestaurant(String restaurantId) {
        return restaurantId != null && !restaurantId.isEmpty();
    }

    /**
     * Verify menu item details from restaurant service
     * @param restaurantId Restaurant ID
     * @param itemId Item ID
     * @param authToken Authentication token
     * @return Menu item details
     */
    default Map<String, Object> getMenuItemDetails(String restaurantId, String itemId, String authToken) {
        // Default implementation could be added if needed
        return new HashMap<>();
    }

    /**
     * Send order notification
     * @param orderId Order ID
     * @param userId User ID
     * @param restaurantId Restaurant ID
     * @param type Notification type
     * @param authToken Authentication token
     */
    default void sendOrderNotification(String orderId, String userId, String restaurantId,
                                       String type, String authToken) {
        // Default implementation could be added if needed
    }

    /**
     * Notify about order status change
     * @param order The order that changed
     * @param oldStatus The previous status (nullable)
     */
    default void notifyOrderStatusChange(Order order, String oldStatus) {
        // Default implementation could be added if needed
    }
}
