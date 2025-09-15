package com.foodieapp.user.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * Client for User Service to communicate with Restaurant Service
 * for authorization and validation purposes only.
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
     * Single source of truth for verifying restaurant ownership
     * Used for authorization purposes
     */
    public boolean verifyRestaurantOwnership(String restaurantId, String userId, String authToken) {
        String cacheKey = "restaurant_ownership_" + restaurantId + "_" + userId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                String path = "/api/v1/validate/ownership/" + restaurantId + "/user/" + userId;
                Map<String, Object> response = getForObject(path, Map.class, authToken);

                return response != null && Boolean.TRUE.equals(response.get("isOwner"));
            } catch (Exception e) {
                logger.error("Error verifying restaurant ownership: {}", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Verify if a restaurant exists
     * Used for validation purposes only
     */
    public boolean verifyRestaurantExists(String restaurantId, String authToken) {
        String cacheKey = "restaurant_existence_" + restaurantId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                String path = "/api/v1/validate/restaurant/" + restaurantId;
                Map<String, Object> response = getForObject(path, Map.class, authToken);

                return response != null && Boolean.TRUE.equals(response.get("exists"));
            } catch (Exception e) {
                logger.error("Error verifying restaurant existence: {}", e.getMessage());
                return false;
            }
        });
    }
}
