package com.foodieapp.restaurant.controller.validation;

import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.repository.MenuItemRepository;
import com.foodieapp.restaurant.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/validate")
public class ValidationController {
    private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Single consolidated endpoint for all restaurant validation
     * Returns restaurant details with validation info
     */
    @GetMapping("/restaurant/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateRestaurant(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            response.put("exists", true);
            response.put("ownerId", restaurant.getOwnerId());
            response.put("verified", restaurant.getIsVerified());
            response.put("active", restaurant.getIsActive());
            response.put("name", restaurant.getName());
            return ResponseEntity.ok(ApiResponse.success("Restaurant validation successful", response));
        } catch (Exception e) {
            response.put("exists", false);
            return ResponseEntity.ok(ApiResponse.success("Restaurant not found", response));
        }
    }

    /**
     * Verify if a user owns a restaurant
     * This endpoint is called by other services to check ownership
     */
    @GetMapping("/ownership/{restaurantId}/user/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOwnership(
            @PathVariable String restaurantId,
            @PathVariable String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get restaurant details
            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);

            // Check if the restaurant exists and is active
            if (restaurant == null || !restaurant.getIsActive()) {
                response.put("exists", false);
                response.put("isOwner", false);
                response.put("message", "Restaurant not found or inactive");
                return ResponseEntity.ok(ApiResponse.success("Restaurant validation failed", response));
            }

            // Verify ownership by comparing user ID with restaurant owner ID
            boolean isOwner = userId != null && userId.equals(restaurant.getOwnerId());

            // Populate response
            response.put("exists", true);
            response.put("isOwner", isOwner);
            response.put("restaurantName", restaurant.getName());
            response.put("verified", restaurant.getIsVerified());
            response.put("active", restaurant.getIsActive());

            if (!isOwner) {
                response.put("message", "User is not the owner of this restaurant");
            }

            return ResponseEntity.ok(ApiResponse.success("Ownership validation completed", response));

        } catch (Exception e) {
            // Handle exceptions
            response.put("exists", false);
            response.put("isOwner", false);
            response.put("message", "Error verifying ownership: " + e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Error verifying ownership", "VALIDATION_ERROR", response));
        }
    }

    /**
     * Validate a menu item
     * This endpoint is called by the order service to verify menu items
     */
    @GetMapping("/restaurant/{restaurantId}/menu/{itemId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateMenuItem(
            @PathVariable String restaurantId,
            @PathVariable String itemId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get menu item from the restaurant
            MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(itemId, restaurantId);

            if (menuItem == null) {
                response.put("valid", false);
                response.put("message", "Menu item not found");
                return ResponseEntity.ok(ApiResponse.success("Menu item validation failed", response));
            }

            // Check if the item is available
            response.put("valid", true);
            response.put("available", menuItem.isAvailable());
            response.put("name", menuItem.getName());
            response.put("price", menuItem.getPrice());
            response.put("description", menuItem.getDescription());
            response.put("category", menuItem.getCategory());

            return ResponseEntity.ok(ApiResponse.success("Menu item validation successful", response));
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Error validating menu item: " + e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Error validating menu item", "VALIDATION_ERROR", response));
        }
    }
}
