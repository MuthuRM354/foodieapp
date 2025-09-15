package com.foodieapp.restaurant.controller.admin;

import com.foodieapp.restaurant.dto.response.ApiResponse;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.model.Restaurant;
import com.foodieapp.restaurant.service.AuthorizationService;
import com.foodieapp.restaurant.service.RestaurantService;
import com.foodieapp.restaurant.service.external.ThirdPartyApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ThirdPartyApiClient thirdPartyApiClient;

    @Autowired
    private AuthorizationService authorizationService;

    @DeleteMapping("/restaurants/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(@PathVariable String id) {
        // Ensure admin privileges using centralized service
        authorizationService.requireAdminRole();

        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully", null));
    }

    @PutMapping("/restaurants/{id}/verify")
    public ResponseEntity<ApiResponse<RestaurantDetailResponse>> verifyRestaurant(@PathVariable String id) {
        // Ensure admin privileges using centralized service
        authorizationService.requireAdminRole();

        Restaurant restaurant = restaurantService.getRestaurantById(id);
        restaurant.setIsVerified(true);
        RestaurantDetailResponse response = restaurantService.updateRestaurant(id, restaurant);
        return ResponseEntity.ok(ApiResponse.success("Restaurant verified successfully", response));
    }

    @GetMapping("/ingredients")
    public ResponseEntity<ApiResponse<List<String>>> getAllIngredients() {
        // Ensure admin privileges using centralized service
        authorizationService.requireAdminRole();

        List<String> ingredients = thirdPartyApiClient.getIngredients();
        return ResponseEntity.ok(ApiResponse.success("All ingredients retrieved successfully", ingredients));
    }
}
