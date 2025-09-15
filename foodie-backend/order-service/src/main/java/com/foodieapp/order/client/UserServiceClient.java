package com.foodieapp.order.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class UserServiceClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${user.service.url}") String userServiceUrl) {
        super(restTemplate, userServiceUrl);
    }

    /**
     * Validate a token with the user service - this is the single source of truth for token validation
     * @param token Authentication token
     * @return Map containing validation results
     */
    public Map<String, Object> validateToken(String token) {
        String cacheKey = "token_validation_" + token;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                return getForObject("/api/v1/auth/validate-token", Map.class, token);
            } catch (Exception e) {
                logger.error("Token validation failed: {}", e.getMessage());
                return Collections.singletonMap("valid", false);
            }
        });
    }

    /**
     * Extract user ID from token
     * @param token Authentication token
     * @return User ID or null if invalid
     */
    public String extractUserIdFromToken(String token) {
        Map<String, Object> tokenInfo = validateToken(token);
        if (Boolean.TRUE.equals(tokenInfo.get("valid")) && tokenInfo.containsKey("userId")) {
            return tokenInfo.get("userId").toString();
        }
        return null;
    }

    /**
     * Get user details by ID
     * @param userId User ID
     * @param token Authentication token
     * @return User details
     */
    public Map<String, Object> getUserDetails(String userId, String token) {
        String cacheKey = "user_details_" + userId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                String path = "/api/v1/users/" + userId;
                Map<String, Object> response = getForObject(path, Map.class, token);

                if (response != null && response.containsKey("data")) {
                    return (Map<String, Object>) response.get("data");
                }
                return Collections.singletonMap("exists", false);
            } catch (Exception e) {
                logger.error("Failed to get user details: {}", e.getMessage());
                return Collections.singletonMap("exists", false);
            }
        });
    }

    /**
     * Verify user exists
     * @param userId User ID
     * @param token Authentication token
     * @return true if user exists
     */
    public boolean verifyUserExists(String userId, String token) {
        Map<String, Object> userDetails = getUserDetails(userId, token);
        return userDetails != null && Boolean.TRUE.equals(userDetails.get("exists"));
    }

    /**
     * Check if user has admin role
     * @param token Authentication token
     * @return true if user is admin
     */
    public boolean isUserAdmin(String token) {
        Map<String, Object> tokenInfo = validateToken(token);
        if (Boolean.TRUE.equals(tokenInfo.get("valid")) && tokenInfo.containsKey("roles")) {
            try {
                @SuppressWarnings("unchecked")
                Iterable<String> roles = (Iterable<String>) tokenInfo.get("roles");
                for (String role : roles) {
                    if ("ROLE_ADMIN".equals(role)) {
                        return true;
                    }
                }
            } catch (ClassCastException e) {
                logger.error("Failed to parse roles from token: {}", e.getMessage());
            }
        }
        return false;
    }
}
