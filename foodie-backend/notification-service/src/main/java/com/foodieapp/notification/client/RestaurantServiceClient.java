package com.foodieapp.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RestaurantServiceClient extends AbstractServiceClient {
    public RestaurantServiceClient(
            RestTemplate restTemplate,
            @Value("${restaurant.service.url}") String restaurantServiceUrl) {
        super(restTemplate, restaurantServiceUrl);
    }

    /**
     * Get restaurant details
     */
    public Map<String, Object> getRestaurantDetails(String restaurantId) {
        String cacheKey = "restaurant_details_" + restaurantId;

        return getCachedOrCompute(cacheKey, () ->
            getForObject("/api/v1/restaurants/" + restaurantId, Map.class)
        );
    }

    /**
     * Get restaurant owner details
     */
    public Map<String, Object> getRestaurantOwner(String restaurantId) {
        String cacheKey = "restaurant_owner_" + restaurantId;

        return getCachedOrCompute(cacheKey, () ->
            getForObject("/api/v1/restaurants/" + restaurantId + "/owner", Map.class)
        );
    }

    /**
     * Verify restaurant exists
     */
    public boolean restaurantExists(String restaurantId) {
        String cacheKey = "restaurant_exists_" + restaurantId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                Map<String, Object> response = getForObject("/api/v1/validate/restaurant/" + restaurantId, Map.class);
                if (response != null && response.containsKey("exists")) {
                    return Boolean.TRUE.equals(response.get("exists"));
                }
                return false;
            } catch (Exception e) {
                logger.error("Error checking if restaurant exists: {}", e.getMessage());
                return false;
            }
        });
    }
}
