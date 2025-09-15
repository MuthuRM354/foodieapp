package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.exception.RestaurantNotFoundException;
import com.foodieapp.restaurant.exception.UnauthorizedException;
import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestaurantOwnerVerificationService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantOwnerVerificationService.class);

    private final RestaurantRepository restaurantRepository;
    private final AuthorizationService authorizationService;

    @Autowired
    public RestaurantOwnerVerificationService(
            RestaurantRepository restaurantRepository,
            AuthorizationService authorizationService) {
        this.restaurantRepository = restaurantRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * Verify if current user is the owner of the restaurant
     */
    public boolean verifyRestaurantOwnership(String restaurantId) {
        String userId = authorizationService.getCurrentUserId();
        if (userId == null) {
            return false;
        }

        try {
            Restaurant restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

            return restaurant.getOwnerId().equals(userId) || authorizationService.isCurrentUserAdmin();
        } catch (Exception e) {
            logger.error("Error verifying restaurant ownership: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Require restaurant ownership or throw exception
     */
    public void requireRestaurantOwnership(String restaurantId) {
        if (!verifyRestaurantOwnership(restaurantId)) {
            throw new UnauthorizedException("You don't have permission to access this restaurant");
        }
    }
}