package com.foodieapp.payment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Component
public class UserServiceClient extends ServiceClient {

    public UserServiceClient(
            RestTemplate restTemplate,
            @Value("${services.user.url:${user.service.url}}") String userServiceUrl) {
        super(restTemplate, userServiceUrl);
    }

    /**
     * Validate a token with the user service
     */
    public Map<String, Object> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            logger.warn("Attempt to validate null or empty token");
            return Collections.singletonMap("valid", false);
        }

        String cacheKey = "token_validation_" + token;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                logger.debug("Validating token with user service");
                Map<String, Object> result = getForObject("/api/v1/auth/validate-token", Map.class, token);

                if (result == null) {
                    logger.warn("Null response from token validation");
                    return Collections.singletonMap("valid", false);
                }

                logger.debug("Token validation result: {}", result.get("valid"));
                return result;
            } catch (Exception e) {
                return handleApiCallException("token validation", e,
                        Collections.singletonMap("valid", false));
            }
        });
    }

    /**
     * Extract user ID from authentication token
     */
    public String extractUserIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            logger.warn("Attempt to extract user ID from null or empty token");
            return null;
        }

        // Strip "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            Map<String, Object> tokenData = validateToken(token);

            if (tokenData != null && Boolean.TRUE.equals(tokenData.get("valid"))) {
                Object userId = tokenData.get("userId");
                if (userId != null) {
                    return userId.toString();
                }
            }

            logger.warn("Could not extract user ID from token. Token validation failed or missing userId");
            return null;
        } catch (Exception e) {
            logger.error("Error extracting user ID from token", e);
            return null;
        }
    }

    /**
     * Get user details by ID
     */
    public Map<String, Object> getUserById(String userId, String token) {
        if (userId == null || userId.isEmpty()) {
            logger.warn("Attempt to get user with null or empty ID");
            return Collections.emptyMap();
        }

        String cacheKey = "user_details_" + userId;

        return getCachedOrCompute(cacheKey, () -> {
            try {
                logger.debug("Getting user details for ID: {}", userId);
                Map<String, Object> result = getForObject("/api/v1/auth/users/" + userId, Map.class, token);

                if (result == null) {
                    logger.warn("User not found with ID: {}", userId);
                    return Collections.emptyMap();
                }

                return result;
            } catch (Exception e) {
                return handleApiCallException("getting user details", e, Collections.emptyMap());
            }
        });
    }

    /**
     * Get user details (alias for getUserById for compatibility)
     */
    public Map<String, Object> getUserDetails(String userId) {
        return getUserById(userId, null);
    }

    /**
     * Check if user has payment method
     */
    public boolean hasPaymentMethod(String userId, String paymentMethodType, String token) {
        if (userId == null || userId.isEmpty() || paymentMethodType == null || paymentMethodType.isEmpty()) {
            logger.warn("Invalid parameters for checking payment method");
            return false;
        }

        try {
            logger.debug("Checking if user {} has payment method: {}", userId, paymentMethodType);
            Map<String, Object> response = getForObject(
                    "/api/v1/auth/users/" + userId + "/payment-methods/" + paymentMethodType,
                    Map.class,
                    token);

            return response != null && Boolean.TRUE.equals(response.get("exists"));
        } catch (Exception e) {
            return handleApiCallException("checking payment method", e, false);
        }
    }

    /**
     * Check if user is allowed to perform payment operations
     */
    public boolean canUserPerformPayment(String userId, String token) {
        if (userId == null || userId.isEmpty()) {
            logger.warn("Attempt to check payment permission with null or empty user ID");
            return false;
        }

        try {
            logger.debug("Checking if user {} can perform payment", userId);
            Map<String, Object> response = getForObject(
                    "/api/v1/auth/users/" + userId + "/can-pay",
                    Map.class,
                    token);

            return response != null && Boolean.TRUE.equals(response.get("canPay"));
        } catch (Exception e) {
            return handleApiCallException("checking payment permission", e, false);
        }
    }
}
