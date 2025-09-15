package com.foodieapp.order.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * Client for Order Service to communicate with Restaurant Service
 */
@Component
public class RestaurantServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceClient.class);

    public RestaurantServiceClient(
            RestTemplate restTemplate,
            @Value("${restaurant.service.url}") String restaurantServiceUrl) {
        super(restTemplate, restaurantServiceUrl);
    }

    /**
     * Get restaurant information by ID
     */
    public Map<String, Object> getRestaurantInfo(String restaurantId, String authToken) {
        String cacheKey = "restaurant_info_" + restaurantId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                return getForObject("/api/v1/restaurants/" + restaurantId, Map.class, authToken);
            } catch (Exception e) {
                logger.error("Error getting restaurant info: {}", e.getMessage());
                return Collections.singletonMap("exists", false);
            }
        });
    }

    /**
     * Verify restaurant ownership
     * Used for authorization purposes
     */
    public Map<String, Object> verifyOwnership(String restaurantId, String userId, String authToken) {
        String cacheKey = "restaurant_ownership_" + restaurantId + "_" + userId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                return getForObject(
                        "/api/v1/validate/ownership/" + restaurantId + "/user/" + userId,
                        Map.class,
                        authToken);
            } catch (Exception e) {
                logger.error("Error verifying restaurant ownership: {}", e.getMessage());
                return Collections.singletonMap("isOwner", false);
            }
        });
    }

    /**
     * Verify if a restaurant exists
     * Used for validation purposes only
     */
    public boolean verifyRestaurantExists(String restaurantId, String authToken) {
        Map<String, Object> restaurantInfo = getRestaurantInfo(restaurantId, authToken);
        return restaurantInfo != null && Boolean.TRUE.equals(restaurantInfo.get("exists"));
    }

    /**
     * Get menu item details from restaurant
     */
    public Map<String, Object> getMenuItemDetails(String restaurantId, String itemId, String authToken) {
        String cacheKey = "menu_item_" + restaurantId + "_" + itemId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                return getForObject(
                        "/api/v1/restaurants/" + restaurantId + "/menu-items/" + itemId,
                        Map.class,
                        authToken);
            } catch (Exception e) {
                logger.error("Error getting menu item details: {}", e.getMessage());
                return Collections.singletonMap("exists", false);
            }
        });
    }
}
