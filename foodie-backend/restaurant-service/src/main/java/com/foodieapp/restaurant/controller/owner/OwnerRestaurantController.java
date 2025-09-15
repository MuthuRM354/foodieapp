package com.foodieapp.restaurant.controller.owner;

import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.dto.request.RestaurantRequest;
import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.service.AuthorizationService;
import com.foodieapp.restaurant.service.RestaurantOwnerVerificationService;
import com.foodieapp.restaurant.service.RestaurantService;
import com.foodieapp.restaurant.util.mapper.EntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/owner/restaurants")
public class OwnerRestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(OwnerRestaurantController.class);

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private RestaurantOwnerVerificationService ownerVerificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<RestaurantDetailResponse>> createRestaurant(
            @Valid @RequestBody RestaurantRequest request,
            @RequestHeader("Authorization") String authHeader) {

        // Get user ID from centralized AuthorizationService
        String ownerId = authorizationService.getCurrentUserId();

        if (ownerId == null || ownerId.isEmpty()) {
            logger.error("User ID could not be determined from authentication token.");
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("User ID could not be determined", "AUTHENTICATION_ERROR"));
        }

        // Set owner ID in the request
        request.setOwnerId(ownerId);
        logger.info("Creating restaurant for owner ID: {}", ownerId);

        // Convert request to Restaurant entity
        Restaurant restaurant = EntityMapper.toRestaurantEntity(request);

        // Pass the Restaurant entity to the service
        RestaurantDetailResponse response = restaurantService.createRestaurant(restaurant);
        return ResponseEntity.ok(ApiResponse.success("Restaurant created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getOwnerRestaurants() {
        // Get user ID from centralized AuthorizationService
        String ownerId = authorizationService.getCurrentUserId();
        if (ownerId == null || ownerId.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("User ID could not be determined", "AUTHENTICATION_ERROR"));
        }

        List<RestaurantDetailResponse> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success("Owner restaurants retrieved successfully", restaurants));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDetailResponse>> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantRequest request) {

        // Use centralized method for ownership verification
        ownerVerificationService.requireRestaurantOwnership(id);

        // Retrieve existing restaurant
        Restaurant existingRestaurant = restaurantService.getRestaurantById(id);

        // Preserve the original owner ID
        request.setOwnerId(existingRestaurant.getOwnerId());

        Restaurant restaurant = EntityMapper.toRestaurantEntity(request);
        RestaurantDetailResponse response = restaurantService.updateRestaurant(id, restaurant);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", response));
    }

    @PostMapping("/{restaurantId}/menu")
    public ResponseEntity<ApiResponse<MenuItem>> addMenuItem(
            @PathVariable String restaurantId,
            @Valid @RequestBody MenuItem menuItem) {

        // Use centralized method for ownership verification
        ownerVerificationService.requireRestaurantOwnership(restaurantId);

        MenuItem created = restaurantService.addMenuItem(restaurantId, menuItem);
        return ResponseEntity.ok(ApiResponse.success("Menu item added successfully", created));
    }

    @PutMapping("/{restaurantId}/menu/{itemId}")
    public ResponseEntity<ApiResponse<MenuItem>> updateMenuItem(
            @PathVariable String restaurantId,
            @PathVariable String itemId,
            @Valid @RequestBody MenuItem menuItem) {

        // Use centralized method for ownership verification
        ownerVerificationService.requireRestaurantOwnership(restaurantId);

        // Ensure menuItem has correct restaurantId and itemId
        menuItem.setRestaurantId(restaurantId);
        menuItem.setId(itemId);

        // Call service to update the menu item
        MenuItem updatedItem = restaurantService.updateMenuItem(restaurantId, itemId, menuItem);
        return ResponseEntity.ok(ApiResponse.success("Menu item updated successfully", updatedItem));
    }

    @DeleteMapping("/{restaurantId}/menu/{itemId}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(
            @PathVariable String restaurantId,
            @PathVariable String itemId) {

        // Use centralized method for ownership verification
        ownerVerificationService.requireRestaurantOwnership(restaurantId);

        // Call service to delete the menu item
        restaurantService.deleteMenuItem(restaurantId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Menu item deleted successfully", null));
    }

    // Debug endpoint
    @GetMapping("/debug/current-user")
    public ResponseEntity<Map<String, Object>> debugCurrentUser() {
        Map<String, Object> response = new HashMap<>();
        String userId = authorizationService.getCurrentUserId();
        boolean isAdmin = authorizationService.isCurrentUserAdmin();
        boolean isRestaurantOwner = authorizationService.isCurrentUserRestaurantOwner();

        response.put("userId", userId);
        response.put("isAdmin", isAdmin);
        response.put("isRestaurantOwner", isRestaurantOwner);

        return ResponseEntity.ok(response);
    }
}
