package com.foodieapp.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class RestaurantServiceClient extends ServiceClient {

    public RestaurantServiceClient(RestTemplate restTemplate,
                                   @Value("${services.restaurant.url:${restaurant.service.url}}") String baseUrl) {
        super(restTemplate, baseUrl);
    }

    /**
     * Validate if restaurant exists and is active
     */
    public Map<String, Object> validateRestaurant(String restaurantId) {
        if (restaurantId == null || restaurantId.isEmpty()) {
            logger.warn("Attempt to validate restaurant with null or empty ID");
            return Collections.singletonMap("exists", false);
        }

        String cacheKey = "restaurant_validation_" + restaurantId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                logger.debug("Validating restaurant with ID: {}", restaurantId);
                ResponseEntity<Map> response = exchange(
                        "/api/v1/validate/restaurant/" + restaurantId,
                        HttpMethod.GET,
                        null,
                        Map.class);

                return response.getBody();
            } catch (Exception e) {
                return handleApiCallException("validating restaurant", e, Collections.singletonMap("exists", false));
            }
        });
    }

    /**
     * Verify if user has ownership of restaurant
     */
    public Map<String, Object> verifyRestaurantOwnership(String restaurantId, String userId, String token) {
        if (restaurantId == null || restaurantId.isEmpty() || userId == null || userId.isEmpty()) {
            logger.warn("Invalid parameters for verifying restaurant ownership");
            return Collections.singletonMap("isOwner", false);
        }

        String cacheKey = "restaurant_ownership_" + restaurantId + "_" + userId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                logger.debug("Verifying restaurant ownership: Restaurant={}, User={}", restaurantId, userId);
                ResponseEntity<Map> response = exchange(
                        "/api/v1/validate/ownership/" + restaurantId + "/user/" + userId,
                        HttpMethod.GET,
                        null,
                        Map.class,
                        token);

                return response.getBody();
            } catch (Exception e) {
                return handleApiCallException("verifying restaurant ownership", e,
                        Collections.singletonMap("isOwner", false));
            }
        });
    }

    /**
     * Get restaurant details
     */
    public Map<String, Object> getRestaurantDetails(String restaurantId, String token) {
        if (restaurantId == null || restaurantId.isEmpty()) {
            logger.warn("Attempt to get restaurant details with null or empty ID");
            return Collections.emptyMap();
        }

        String cacheKey = "restaurant_details_" + restaurantId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                logger.debug("Getting restaurant details for ID: {}", restaurantId);
                ResponseEntity<Map> response = exchange(
                        "/api/v1/restaurants/" + restaurantId,
                        HttpMethod.GET,
                        null,
                        Map.class,
                        token);

                return response.getBody();
            } catch (Exception e) {
                return handleApiCallException("getting restaurant details", e, Collections.emptyMap());
            }
        });
    }
}
