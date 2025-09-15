package com.foodieapp.restaurant.controller.customer;

import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.dto.response.RestaurantDTO;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
public class CustomerRestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerRestaurantController.class);

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getAllRestaurants() {
        List<RestaurantDetailResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", restaurants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantDetailResponse>> getRestaurantById(@PathVariable String id) {
        RestaurantDetailResponse restaurant = restaurantService.getRestaurantDetailById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant details retrieved successfully", restaurant));
    }

    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getRestaurantsByCuisine(@PathVariable String cuisine) {
        List<RestaurantDetailResponse> restaurants = restaurantService.getRestaurantsByCuisine(cuisine);
        return ResponseEntity.ok(ApiResponse.success("Restaurants by cuisine retrieved successfully", restaurants));
    }

    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<ApiResponse<List<MenuItem>>> getRestaurantMenu(@PathVariable String restaurantId) {
        List<MenuItem> menuItems = restaurantService.getMenuItems(restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant menu retrieved successfully", menuItems));
    }

    /**
     * Consolidated endpoint for different restaurant discovery modes
     *
     * @param type The type of restaurant query: "popular", "nearby", "new", "top-rated"
     * @param latitude Latitude for nearby search (optional)
     * @param longitude Longitude for nearby search (optional)
     * @param radiusInKm Radius in kilometers for nearby search (optional)
     * @param limit Maximum number of results to return (optional)
     * @return List of restaurant details matching the criteria
     */
    @GetMapping("/discover")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> discoverRestaurants(
            @RequestParam String type,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "10") double radiusInKm,
            @RequestParam(required = false, defaultValue = "10") int limit) {

        List<RestaurantDetailResponse> restaurants;
        String successMessage;

        switch (type.toLowerCase()) {
            case "popular":
                restaurants = restaurantService.getPopularRestaurants(limit);
                successMessage = "Popular restaurants retrieved successfully";
                break;

            case "nearby":
                if (latitude == null || longitude == null) {
                    return ResponseEntity.badRequest().body(
                        ApiResponse.error("Latitude and longitude are required for nearby search", "BAD_REQUEST"));
                }
                restaurants = restaurantService.getNearbyRestaurants(latitude, longitude, radiusInKm, limit);
                successMessage = "Nearby restaurants retrieved successfully";
                break;

            case "new":
                restaurants = restaurantService.getNewRestaurants(limit);
                successMessage = "New restaurants retrieved successfully";
                break;

            case "top-rated":
                restaurants = restaurantService.getTopRatedRestaurants(limit);
                successMessage = "Top rated restaurants retrieved successfully";
                break;

            default:
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Invalid restaurant discovery type. Use 'popular', 'nearby', 'new', or 'top-rated'",
                                     "INVALID_TYPE"));
        }

        return ResponseEntity.ok(ApiResponse.success(successMessage, restaurants));
    }

    // Backward compatibility endpoints - explicitly call the consolidated endpoint

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getPopularRestaurants(
            @RequestParam(defaultValue = "10") int limit) {
        // Delegate to discover endpoint with type=popular
        return discoverRestaurants("popular", null, null, 10, limit);
    }

    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getNearbyRestaurants(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double radiusInKm,
            @RequestParam(defaultValue = "10") int limit) {
        // Delegate to discover endpoint with type=nearby
        return discoverRestaurants("nearby", latitude, longitude, radiusInKm, limit);
    }

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getNewRestaurants(
            @RequestParam(defaultValue = "10") int limit) {
        // Delegate to discover endpoint with type=new
        return discoverRestaurants("new", null, null, 10, limit);
    }

    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<RestaurantDetailResponse>>> getTopRatedRestaurants(
            @RequestParam(defaultValue = "10") int limit) {
        // Delegate to discover endpoint with type=top-rated
        return discoverRestaurants("top-rated", null, null, 10, limit);
    }

    // Featured restaurants endpoint
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<RestaurantDTO>>> getFeaturedRestaurants() {
        try {
            List<RestaurantDTO> restaurants = restaurantService.getFeaturedRestaurants();
            return ResponseEntity.ok(ApiResponse.success("Featured restaurants retrieved successfully",
                restaurants.isEmpty() ? new ArrayList<>() : restaurants));
        } catch (Exception e) {
            logger.error("Error retrieving featured restaurants: {}", e.getMessage(), e);
            return ResponseEntity.ok(
                ApiResponse.error("Failed to retrieve featured restaurants", "INTERNAL_SERVER_ERROR"));
        }
    }
}
