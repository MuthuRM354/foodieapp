package com.foodieapp.user.util;

import com.foodieapp.user.exception.UnauthorizedException;
import com.foodieapp.user.model.Role;
import com.foodieapp.user.model.User;
import com.foodieapp.user.security.JwtService;
import com.foodieapp.user.service.user.UserService;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

    private final UserService userService;
    private final JwtService jwtService;

    public TokenUtil(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Extract token from Authorization header
     */
    public String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid authorization token");
        }
        return authHeader.substring(7);
    }

    /**
     * Get user from Authorization header
     */
    public User getUserFromToken(String authHeader) {
        String token = extractToken(authHeader);
        return userService.findUserByJwtToken(token);
    }

    /**
     * Validate that the user has admin role
     */
    public User validateAdminAccess(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (!user.hasRole(Role.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only admin users can access this endpoint");
        }
        return user;
    }

    /**
     * Validate that the user has restaurant owner role
     */
    public User validateRestaurantOwnerAccess(String authHeader) {
        User user = getUserFromToken(authHeader);
        if (!user.hasRole(Role.ROLE_RESTAURANT_OWNER) && !user.hasRole(Role.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only restaurant owners or admin users can access this endpoint");
        }
        return user;
    }

    /**
     * Check if user owns a specific restaurant
     */
    public boolean isRestaurantOwner(User user, String restaurantId) {
        // This could call the RestaurantServiceClient to check ownership
        return true; // Implementation would depend on your business logic
    }

    /**
     * Check if token is valid
     */
    public boolean isTokenValid(String token) {
        return jwtService.isTokenValid(token);
    }
}
