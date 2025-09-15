package com.foodieapp.restaurant.interceptor;

import com.foodieapp.restaurant.exception.UnauthorizedException;
import com.foodieapp.restaurant.service.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RestaurantOwnerInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnerInterceptor.class);

    private final AuthorizationService authorizationService;

    @Autowired
    public RestaurantOwnerInterceptor(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Extract token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Authorization header found or invalid format");
            throw new UnauthorizedException("Authorization required");
        }

        // Use centralized service to check if user is a restaurant owner
        try {
            // This will throw an exception if user doesn't have required role
            authorizationService.requireRestaurantOwnerRole();
            String userId = null;
            try {
                userId = authorizationService.getCurrentUserId();
            } catch (Exception e) {
                logger.warn("Error getting user ID", e);
            }
            logger.info("User {} authenticated as restaurant owner", userId);
            return true;
        } catch (UnauthorizedException e) {
            String userId = null;
            try {
                userId = authorizationService.getCurrentUserId();
            } catch (Exception ex) {
                logger.warn("Error getting user ID", ex);
            }
            logger.warn("User {} is not a restaurant owner", userId);
            throw e;
        }
    }
}
