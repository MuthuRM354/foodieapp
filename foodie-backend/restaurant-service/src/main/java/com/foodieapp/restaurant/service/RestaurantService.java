package com.foodieapp.restaurant.service;

import com.foodieapp.restaurant.dto.response.RestaurantDTO;
import com.foodieapp.restaurant.dto.response.RestaurantDetailResponse;
import com.foodieapp.restaurant.model.MenuItem;
import com.foodieapp.restaurant.model.Restaurant;

import java.util.List;

public interface RestaurantService {
    // Restaurant operations
    RestaurantDetailResponse createRestaurant(Restaurant restaurant);
    Restaurant getRestaurantById(String id);
    RestaurantDetailResponse getRestaurantDetailById(String id);
    List<RestaurantDetailResponse> getAllRestaurants();
    List<RestaurantDetailResponse> getRestaurantsByOwner(String ownerId);
    List<RestaurantDetailResponse> getRestaurantsByCuisine(String cuisine);
    RestaurantDetailResponse updateRestaurant(String id, Restaurant restaurant);
    void deleteRestaurant(String id);

    // Menu operations
    MenuItem addMenuItem(String restaurantId, MenuItem menuItem);
    MenuItem getMenuItem(String restaurantId, String itemId);
    MenuItem updateMenuItem(String restaurantId, String itemId, MenuItem menuItem);
    void deleteMenuItem(String restaurantId, String itemId);
    List<MenuItem> getMenuItems(String restaurantId);

    // Special queries
    List<RestaurantDetailResponse> getPopularRestaurants(int limit);
    List<RestaurantDetailResponse> getNearbyRestaurants(double latitude, double longitude, double radiusInKm, int limit);
    List<RestaurantDetailResponse> getNewRestaurants(int limit);
    List<RestaurantDetailResponse> getTopRatedRestaurants(int limit);
    List<RestaurantDTO> getFeaturedRestaurants();

    // Note: getPopularCuisines() is not part of this interface
    // It's implemented in RestaurantServiceImpl but not part of the contract
}
