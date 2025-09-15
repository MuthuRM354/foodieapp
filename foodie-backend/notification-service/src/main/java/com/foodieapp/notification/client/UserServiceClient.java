package com.foodieapp.notification.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserServiceClient extends AbstractServiceClient {

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${user.service.url}") String userServiceUrl) {
        super(restTemplate, userServiceUrl);
    }

    /**
     * Get user details
     */
    public Map<String, Object> getUserDetails(String userId) {
        String cacheKey = "user_details_" + userId;

        return getCachedOrCompute(cacheKey, () ->
            // Removed redundant try-catch
            getForObject("/api/v1/auth/users/" + userId, Map.class)
        );
    }

    /**
     * Validate token
     */
    public Map<String, Object> validateToken(String token) {
        String cacheKey = "token_validation_" + token;

        return getCachedOrCompute(cacheKey, () ->
            // Removed redundant try-catch
            getForObject("/api/v1/auth/validate-token", Map.class, token)
        );
    }

    /**
     * Get user contact preferences
     */
    public Map<String, Object> getUserContactPreferences(String userId) {
        String cacheKey = "user_contact_prefs_" + userId;

        return getCachedOrCompute(cacheKey, () ->
            // Removed redundant try-catch
            getForObject("/api/v1/auth/users/" + userId + "/contact-preferences", Map.class)
        );
    }
}
