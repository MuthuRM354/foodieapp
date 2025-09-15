package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.client.UserServiceClient;
import com.foodieapp.restaurant.exception.UnauthorizedException;
import com.foodieapp.restaurant.util.SecurityContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for authorization operations
 */
@Service
public class AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    private final UserServiceClient userServiceClient;

    @Autowired
    public AuthorizationService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    /**
     * Get current user ID - now uses SecurityContextUtils
     */
    public String getCurrentUserId() {
        return SecurityContextUtils.getCurrentUserId();
    }

    /**
     * Check if current user is an admin - now uses SecurityContextUtils
     */
    public boolean isCurrentUserAdmin() {
        return SecurityContextUtils.isCurrentUserAdmin();
    }

    /**
     * Check if current user is restaurant owner - now uses SecurityContextUtils
     */
    public boolean isCurrentUserRestaurantOwner() {
        return SecurityContextUtils.isCurrentUserRestaurantOwner();
    }

    /**
     * Get the current authorization header - now uses SecurityContextUtils
     */
    public String getCurrentAuthHeader() {
        return SecurityContextUtils.getCurrentAuthHeader();
    }

    /**
     * Require admin role or throw exception
     */
    public void requireAdminRole() {
        if (!isCurrentUserAdmin()) {
            throw new UnauthorizedException("Admin role required for this operation");
        }
    }

    /**
     * Require restaurant owner role or throw exception
     */
    public void requireRestaurantOwnerRole() {
        if (!isCurrentUserRestaurantOwner() && !isCurrentUserAdmin()) {
            throw new UnauthorizedException("Restaurant owner role required for this operation");
        }
    }

    /**
     * Validates token through user service
     */
    public Map<String, Object> validateToken(String token) {
        return userServiceClient.validateToken(token);
    }

    /**
     * Extract user ID from token
     */
    public String extractUserIdFromToken(String token) {
        return userServiceClient.extractUserIdFromToken(token);
    }

    /**
     * Get user details from user service
     */
    public Map<String, Object> getUserDetails(String userId) {
        return userServiceClient.getUserDetails(userId);
    }

    /**
     * Verify if user has either of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        // Get the current auth header
        String authHeader = getCurrentAuthHeader();
        if (authHeader == null) {
            return false;
        }

        // Validate the token
        Map<String, Object> tokenInfo = validateToken(authHeader);
        if (!Boolean.TRUE.equals(tokenInfo.get("valid"))) {
            return false;
        }

        // Check if user has any of the specified roles
        Object tokenRoles = tokenInfo.get("roles");
        if (tokenRoles instanceof Iterable) {
            for (String requiredRole : roles) {
                for (Object role : (Iterable<?>) tokenRoles) {
                    if (requiredRole.equals(role.toString())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Require user to have at least one of the specified roles
     */
    public void requireAnyRole(String... roles) {
        if (!hasAnyRole(roles)) {
            throw new UnauthorizedException("Required role missing. Access denied.");
        }
    }
}
